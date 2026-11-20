const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any MyMari data", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("transactions").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_1")
      .set({
        userId: ALICE_UID,
        type: "EXPENSE",
        amount: 3.5,
        category: "Canteen and food",
        paymentMethod: "EcoCash",
        dateString: "2026-11-18",
        timeString: "12:30",
        currency: "USD",
        clientTimestamp: 1700000000000,
        createdAt: new Date(Date.now() - 1000),
        updatedAt: new Date(Date.now() - 1000),
      })
  );
});

test("Authenticated owner: valid profile create and partial update succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Tendai",
      email: "tendai@uz.ac.zw",
      university: "University of Zimbabwe",
      preferredCurrency: "USD",
      currencySymbol: "$",
      startingBalance: 250.0,
      monthlyBudgetTarget: 300.0,
      morningReminderEnabled: true,
      morningReminderTime: "07:30",
      eveningReminderEnabled: true,
      eveningReminderTime: "20:00",
      hasCompletedOnboarding: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Partial update allowed fields
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Tendai M.",
      monthlyBudgetTarget: 350.0,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject ghost field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt overwrite
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Authenticated owner: transactions CRUD + validation + @DocumentId exclusion", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_1")
      .set({
        userId: ALICE_UID,
        type: "EXPENSE",
        amount: 2.5,
        category: "Canteen and food",
        paymentMethod: "EcoCash",
        source: "",
        description: "Lunch at campus",
        notes: "Spoken via voice",
        dateString: "2026-11-18",
        timeString: "13:00",
        currency: "USD",
        clientTimestamp: 1700000000000,
        createdAt: ts,
        updatedAt: ts,
      })
  );

  // Reject negative amount
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_bad")
      .set({
        userId: ALICE_UID,
        type: "EXPENSE",
        amount: -5.0,
        category: "Groceries",
        paymentMethod: "Cash",
        dateString: "2026-11-18",
        timeString: "13:00",
        currency: "USD",
        clientTimestamp: 1700000000000,
        createdAt: ts,
        updatedAt: ts,
      })
  );

  // Reject storing "id" field alongside @DocumentId
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_id_field")
      .set({
        id: "tx_id_field",
        userId: ALICE_UID,
        type: "INCOME",
        amount: 50.0,
        category: "Parents or guardians",
        paymentMethod: "Mukuru",
        dateString: "2026-11-18",
        timeString: "10:00",
        currency: "USD",
        clientTimestamp: 1700000000000,
        createdAt: ts,
        updatedAt: ts,
      })
  );

  // List query succeeds for owner
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("transactions").get()
  );

  // Partial update succeeds
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_1")
      .update({
        amount: 3.0,
        description: "Lunch + drink",
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Delete succeeds
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_1")
      .delete()
  );
});

test("Cross-user isolation: Bob cannot read or write Alice's data", async () => {
  const ts = new Date(Date.now() - 5000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("alice_tx")
      .set({
        userId: ALICE_UID,
        type: "EXPENSE",
        amount: 10.0,
        category: "Groceries",
        paymentMethod: "Cash",
        dateString: "2026-11-18",
        timeString: "09:00",
        currency: "USD",
        clientTimestamp: 1700000000000,
        createdAt: ts,
        updatedAt: ts,
      });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("transactions").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("alice_tx")
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("alice_tx")
      .update({ amount: 999.0, updatedAt: new Date(Date.now() - 1000) })
  );
});

test("Budgets, Savings Goals, Contributions, and Daily Notes rules parity", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("budgets")
      .doc("b_1")
      .set({
        userId: ALICE_UID,
        name: "Food Limit",
        category: "Groceries",
        amount: 80.0,
        period: "MONTHLY",
        createdAt: ts,
        updatedAt: ts,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("savingsGoals")
      .doc("g_1")
      .set({
        userId: ALICE_UID,
        name: "Laptop",
        targetAmount: 450.0,
        currentAmount: 50.0,
        categoryIcon: "savings",
        notes: "For coding classes",
        createdAt: ts,
        updatedAt: ts,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("savingsGoals")
      .doc("g_1")
      .collection("contributions")
      .doc("c_1")
      .set({
        userId: ALICE_UID,
        goalId: "g_1",
        amount: 20.0,
        notes: "Saved from allowance",
        createdAt: ts,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("dailyNotes")
      .doc("2026-11-18")
      .set({
        userId: ALICE_UID,
        dateString: "2026-11-18",
        note: "Walked to campus today to save $1.",
        createdAt: ts,
        updatedAt: ts,
      })
  );

  // Tampered future timestamp rejected
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("dailyNotes")
      .doc("2026-11-19")
      .set({
        userId: ALICE_UID,
        dateString: "2026-11-19",
        note: "Future note",
        createdAt: new Date(Date.now() + 120000),
        updatedAt: new Date(Date.now() + 120000),
      })
  );
});
