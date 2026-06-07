# ProjetHandicap 🎓♿

A desktop application for managing requests and complaints from students with disabilities at UIR.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

---

## What is ProjetHandicap?

ProjetHandicap is a Java desktop application built for the UIR administration to manage accommodation requests and complaints submitted by students with disabilities. It provides two separate interfaces depending on the user's role:

- **Student (PersonneHandicap)** — submit accommodation requests, file complaints, and track their status in real time
- **Administrator** — manage student accounts, process requests, and visualize statistics on an interactive dashboard

---

## Features

| Feature | Status |
|---|---|
| Role-based login (Admin / Student) | ✅ Done |
| Student dashboard — request & complaint tracking | ✅ Done |
| Admin dashboard — statistics by status, type, and month | ✅ Done |
| Submit accommodation requests with supporting documents | ✅ Done |
| File complaints with motif and description | ✅ Done |
| Admin request processing — accept / refuse | ✅ Done |
| Full user account management (CRUD) | ✅ Done |
| Document archiving system | ✅ Done |
| Edit / delete pending requests | ✅ Done |

---

## ⚠️ Known Limitations

This is an academic project developed as part of the Software Engineering course at UIR. The following limitations are known:

- Passwords are stored in plain text — no hashing implemented yet
- No file upload system — supporting documents are referenced by name only
- No email notifications — status updates are visible in-app only
- Single machine only — no shared backend or network support

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 11 |
| UI Framework | Java Swing |
| Database | MySQL 8 |
| DB Access | JDBC (mysql-connector-j 9.6.0) |
| Architecture | MVC |
| IDE | VS Code |

---

## Project Structure

```
ProjetHandicap/
├── src/
│   ├── app/
│   │   └── Main.java                   # Entry point
│   ├── model/
│   │   ├── Utilisateur.java            # Abstract base user class
│   │   ├── Administrateur.java         # Extends Utilisateur
│   │   ├── PersonneHandicap.java       # Extends Utilisateur
│   │   ├── Demande.java                # Accommodation request
│   │   ├── Reclamation.java            # Student complaint
│   │   ├── Archive.java                # Archived document
│   │   ├── TableauDeBord.java          # Dashboard stats model
│   │   └── Statut.java                 # Enum: EN_COURS, ACCEPTEE, REFUSEE, ARCHIVEE
│   ├── dao/
│   │   ├── ConnexionDB.java            # MySQL connection
│   │   ├── UtilisateurDAO.java         # User CRUD
│   │   ├── DemandeDAO.java             # Request CRUD + filters
│   │   ├── ReclamationDAO.java         # Complaint CRUD
│   │   ├── DashboardDAO.java           # Stats queries
│   │   └── ArchiveDAO.java             # Archive CRUD
│   ├── controller/
│   │   ├── AuthController.java         # Authentication logic
│   │   ├── UtilisateurController.java  # Account management
│   │   ├── DemandeController.java      # Request logic
│   │   ├── ReclamationController.java  # Complaint logic
│   │   ├── DashboardController.java    # Stats aggregation
│   │   └── ArchiveController.java      # Archive logic
│   └── view/
│       ├── ConnexionView.java          # Login screen
│       ├── DashboardAdminView.java     # Admin interface
│       ├── EtudiantView.java           # Student interface
│       └── UtilisateurView.java        # Account management (admin)
├── lib/
│   └── mysql-connector-j-9.6.0.jar    # JDBC driver
└── database/
    └── schema.sql                      # Database setup script
```

---

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- MySQL Server 8.x
- VS Code or any Java-compatible IDE

### Setup

```bash
# 1. Clone the repo
git clone https://github.com/<your-username>/ProjetHandicap.git
cd ProjetHandicap

# 2. Import the database
mysql -u root -p < database/schema.sql
```

### Configure the connection

Edit `src/dao/ConnexionDB.java` to match your local MySQL setup:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/gestion_handicap";
private static final String USER     = "root";
private static final String PASSWORD = "your_password";
```

### Compile & Run

```bash
# Compile
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d out/ src/**/*.java

# Run
java -cp "out/:lib/mysql-connector-j-9.6.0.jar" app.Main
```

> On Windows, replace `:` with `;` in the classpath.

---

## Screenshots

Coming soon

---

## Author

Iness K — Computer Engineering Student @ UIR Rabat  
🔗 [GitHub](https://github.com/InessK98)

---

## License

Developed as part of the Software Engineering course at UIR – Université Internationale de Rabat.
