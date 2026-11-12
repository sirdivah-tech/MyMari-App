# MyMari 
### Keep Your Money in Your Mind.

**MyMari** is a student-focused personal finance and money management Android application designed to help university and college students living off campus manage their finances responsibly.

Developed by **Divine Pezari**, MyMari serves as a personal financial diary and budgeting companion, helping students track their income, record daily expenses, monitor their remaining balances, set savings goals and understand their spending habits.

---

## Table of Contents

- [About the Project](#-about-the-project)
- [The Problem](#-the-problem)
- [Our Solution](#-our-solution)
- [Project Objectives](#-project-objectives)
- [Key Features](#-key-features)
- [Target Users](#-target-users)
- [Supported Income Sources](#-supported-income-sources)
- [Technology Stack](#-technology-stack)
- [Application Structure](#-application-structure)
- [How It Works](#-how-it-works)
- [Installation and Setup](#-installation-and-setup)
- [Project Roadmap](#-project-roadmap)
- [Privacy and Security](#-privacy-and-security)
- [Future Improvements](#-future-improvements)
- [Contributing](#-contributing)
- [Acknowledgements & AI Assistance](#-acknowledgements-&-ai-assistance)
- [Developer](#-developer)
- [License](#-license)

---

## About the Project

Living away from home comes with financial independence, but financial independence also comes with responsibility.

Many university and college students receive money from their parents or guardians to cover monthly expenses such as accommodation, groceries, transportation, food, academic requirements and personal needs.

However, keeping track of every purchase and understanding where money goes can be challenging.

A student might receive their monthly allowance at the beginning of the month, only to realise a few weeks later that most of the money has disappeared without a clear understanding of how it was spent.

**MyMari was created to address this challenge.**

It provides students with a simple and organised way to record every financial transaction, monitor their spending and make informed financial decisions.

MyMari is more than an expense tracker. It is a student's money diary, budgeting partner and financial accountability companion.

## The Problem

Students living off campus often experience challenges such as:

- Forgetting to record daily purchases.
- Losing track of small expenses that accumulate over time.
- Spending without considering their remaining monthly budget.
- Struggling to distinguish between essential and unnecessary expenses.
- Not knowing how much money they have left.
- Finding it difficult to save from their monthly allowances.
- Running out of money before the end of the month.
- Having no organised record of their daily financial activities.

These challenges can make financial management difficult, especially for students who depend on money sent from home.

## Our Solution

MyMari provides a centralised platform where students can record and monitor their financial activities.

Whether a student receives money through Mukuru, EcoCash, InnBucks, OneMoney, a bank transfer or cash, they can record the amount received and track how it is spent.

The application helps users answer three important questions:

1. How much money do I currently have?
2. How much money have I spent?
3. How much money do I have remaining?

With daily reminders, budgeting tools and financial insights, MyMari encourages students to develop better spending habits and become financially responsible.

## Project Objectives

The main objectives of MyMari are to:

1. Promote financial literacy and responsibility among students.
2. Help students monitor their income and expenditure.
3. Provide an organised record of daily financial activities.
4. Encourage saving and responsible budgeting.
5. Reduce unnecessary spending through financial awareness.
6. Help students manage their monthly allowances effectively.
7. Encourage consistent financial tracking through daily reminders.

## Key Features

### 1. Financial Dashboard
- View total money received.
- Monitor total expenses.
- Check available balance.
- View monthly budget progress.
- Monitor savings.
- Receive financial status updates.

### 2. Income Tracking
Record money received from different sources, including:
- Mukuru.
- EcoCash.
- InnBucks.
- OneMoney.
- Bank transfers.
- Cash.
- Part-time employment.
- Other income sources.

### 3. Expense Tracking
Record everyday purchases and expenses under organised categories:

| Category | Examples |
|---|---|
| Food & Groceries | Tomatoes, vegetables, bread, meat and groceries |
| Canteen & Snacks | Lunch, breakfast, snacks and drinks |
| Accommodation | Rent, electricity, water and utilities |
| Transport | Commuting and transportation |
| Education | Books, stationery, printing and internet |
| Personal | Toiletries, clothing and personal care |
| Other | Medical expenses and emergencies |

### 4. My Money Diary
A daily financial journal that records:
- Money received.
- Money spent.
- Individual purchases.
- Transaction dates and times.
- Daily expenditure.
- Remaining daily balance.

Users can revisit previous days to understand their financial activities.

### 5. Monthly Budget Management
- Set monthly spending limits.
- Allocate money to different categories.
- Monitor category expenditure.
- Receive budget warnings.
- Track remaining monthly funds.

### 6. Savings Tracker
- Set savings goals.
- Record savings contributions.
- Monitor savings progress.
- Track financial targets.

### 7. Daily Reminders
MyMari encourages users to maintain their financial diaries through scheduled notifications.

**Evening reminder – 20:00**

A reminder encouraging students to review their daily spending before going to sleep.

**Morning reminder**

A reminder encouraging students to check their available balance before leaving home.

### 8. Financial Insights and Reports
- Daily spending summaries.
- Weekly expenditure reports.
- Monthly financial reports.
- Spending breakdown by category.
- Income versus expenses.
- Savings progress.
- End-of-month financial summaries.

### 9. Transaction History
- View previous transactions.
- Search transactions.
- Filter by date and category.
- Edit incorrect entries.
- Delete transactions.

---

## Target Users

MyMari is primarily designed for:

- University students.
- College students.
- Students living in off-campus accommodation.
- Students receiving allowances from parents or guardians.
- Young adults learning to manage their finances.
- Individuals looking for a simple personal budgeting tool.

The initial focus is on students in Zimbabwe, with flexibility to support users in other countries through configurable currencies.

## Supported Income Sources

MyMari supports manual recording of money received through different payment channels.

- Mukuru
- EcoCash
- InnBucks
- OneMoney
- Bank accounts
- Cash
- Other income sources

**Note:** MyMari does not automatically access these financial services. Users manually enter the amounts they receive and spend.

---

## Technology Stack

The planned technology stack includes:

| Technology | Purpose |
|---|---|
| Kotlin | Primary Android development language |
| Jetpack Compose | Modern Android user interface |
| Material 3 | Application design system |
| Room Database | Local financial data storage |
| DataStore | Application preferences |
| Firebase Authentication | User authentication |
| Firebase Cloud Firestore | Cloud data synchronisation |
| WorkManager | Background tasks and scheduled reminders |
| Android Studio | Development environment |
| Git & GitHub | Version control and project hosting |

The application follows an offline-first approach, allowing users to manage their finances without requiring a constant internet connection.

*Technology integrations will be introduced progressively as development advances.*

## Application Structure

The intended project structure follows a clean and organised architecture.

```text
MyMari/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── mymari/
│   │       │           ├── data/
│   │       │           │   ├── database/
│   │       │           │   ├── repository/
│   │       │           │   └── preferences/
│   │       │           │
│   │       │           ├── domain/
│   │       │           │   ├── model/
│   │       │           │   └── usecase/
│   │       │           │
│   │       │           ├── ui/
│   │       │           │   ├── dashboard/
│   │       │           │   ├── income/
│   │       │           │   ├── expenses/
│   │       │           │   ├── diary/
│   │       │           │   ├── budget/
│   │       │           │   ├── savings/
│   │       │           │   ├── insights/
│   │       │           │   └── settings/
│   │       │           │
│   │       │           ├── navigation/
│   │       │           └── notifications/
│   │       │
│   │       └── res/
│   │
│   └── build.gradle.kts
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── .gitignore
```

*Note: This is the proposed architecture. The actual folder structure may evolve during implementation.*

## How It Works

The basic financial calculation behind MyMari is:

**Available Balance = Total Income − Total Expenses**

For example:

| Description | Amount |
|---|---:|
| Monthly allowance | $300.00 |
| Groceries | $80.00 |
| Transport | $30.00 |
| Canteen food | $45.00 |
| Personal expenses | $25.00 |
| **Total expenses** | **$180.00** |
| **Remaining balance** | **$120.00** |

Whenever a user records new income or an expense, the application updates their financial balance.

## Installation and Setup

### Prerequisites

Before running MyMari, ensure you have:

- Android Studio installed.
- A computer capable of running Android Studio.
- Android SDK configured.
- Kotlin and Android development support.
- Git installed.
- An Android device or emulator.

### Clone the Repository

```bash
git clone https://github.com/sirdivah-tech/MyMari.git
```

### Open the Project

1. Open Android Studio.
2. Select **Open**.
3. Navigate to the cloned MyMari directory.
4. Allow Gradle to synchronise.
5. Wait for dependencies to finish downloading.

### Run the Application

1. Connect an Android device or start an emulator.
2. Enable USB debugging if using a physical device.
3. Select the target device in Android Studio.
4. Click the Run button.

*Additional Firebase configuration will be required when cloud services are introduced.*

---

## Project Roadmap

MyMari will be developed progressively.

- [ ] Project setup and application architecture.
- [ ] Brand identity and application logo.
- [ ] Splash screen and onboarding.
- [ ] User registration and authentication.
- [ ] Financial dashboard.
- [ ] Income tracking.
- [ ] Expense tracking.
- [ ] My Money Diary.
- [ ] Monthly budgeting.
- [ ] Savings tracker.
- [ ] Transaction history.
- [ ] Daily notifications.
- [ ] Financial analytics.
- [ ] End-of-month reports.
- [ ] PDF and CSV export.
- [ ] Firebase integration.
- [ ] Testing and optimisation.
- [ ] Android APK release.

## Privacy and Security

Financial information is personal and should be protected.

MyMari aims to:

- Keep financial records private.
- Protect user accounts.
- Store financial information securely.
- Avoid collecting unnecessary personal information.
- Never request mobile money PINs or banking passwords.
- Give users control over their financial records.

## Future Improvements

Potential future developments include:

- Optional cloud backup and synchronisation.
- Advanced financial analytics.
- More detailed savings recommendations.
- Recurring expense tracking.
- Custom financial categories.
- Budget sharing and family allowance summaries, with user consent.
- Improved financial insights using intelligent analysis.
- Additional currency support.
- Multi-device synchronisation.

## Contributing

MyMari is an evolving project focused on improving student financial management.

Suggestions, feedback and contributions are welcome.

If you would like to contribute:

1. Fork the repository.
2. Create a new feature branch.
3. Implement your changes.
4. Test your changes.
5. Submit a pull request.
  
## Acknowledgements & AI Assistance

MyMari is designed and developed by **Divine Pezari**, with assistance from AI-powered development tools.

Special acknowledgement to:

- **[Google AI Studio](https://aistudio.google.com/)** – Assisted with application planning, code generation, debugging and development guidance.

AI tools were used as development assistants. Project direction, feature selection, implementation decisions, testing and overall project ownership remain with the developer.

## Developer

**Divine Pezari**

Founder, Project Designer and Developer of MyMari.

MyMari is developed with the vision of helping students become more financially responsible while navigating the challenges of living independently.

## License

The licensing terms for this project are yet to be determined.

---

## The Vision Behind MyMari

Parents and guardians work hard to provide financial support for students studying away from home.

Managing that money wisely is an important part of becoming independent.

MyMari aims to make that responsibility easier by helping students understand their spending, plan their budgets and make every dollar count.

**Your parents send the money. You make the decisions. MyMari helps you make them wisely.**

# MyMari – KEEP YOUR MONEY IN YOUR MIND.

*Designed and developed by Divine Pezari.*
