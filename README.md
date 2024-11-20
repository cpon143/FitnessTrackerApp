# Fitness Tracker App 🏋️‍♂️

A powerful and intuitive **Android application** built with **Kotlin** that helps users track their fitness activities efficiently. The app integrates a **ViewModel** for state management, utilizes the **Repository Pattern**, and employs **SQLite** for data storage.

---

## Features 🚀

1. **Activity Tracking**: Log and manage fitness activities with ease.  
2. **Reset Data Functionality**: Insert all activity data into an SQLite table with a single click using the **Reset** button.  
3. **Dashboard View**: View all activity data in an organized manner.  
4. **Individual Activity Insights**: Display data activity-wise with options like buttons or cards for navigation.  

---

## Table of Contents 📑

1. [Introduction](#introduction)  
2. [Modules/Activity Explanation](#modulesactivity-explanation)  
   - [Home Screen](#home-screen)  
   - [Activity Logger](#activity-logger)  
   - [Dashboard](#dashboard)  
   - [Reset Functionality](#reset-functionality)  
3. [Code Overview](#code-overview)  
4. [Emulator Screenshots](#emulator-screenshots)  
5. [Conclusion & Future Scope](#conclusion--future-scope)  
6. [Project Link](#project-link)  

---

## Introduction

The **Fitness Tracker App** is designed to empower fitness enthusiasts by offering an easy-to-use interface for tracking their workouts and activities. Built with modern Android development practices, this app ensures efficiency, scalability, and a delightful user experience.

---

## Modules/Activity Explanation

### Home Screen  
- Displays a summary of daily activities and quick links to other features.

### Activity Logger  
- Allows users to add and manage their fitness activities.  
- Saves data persistently using **SQLite Database**.

### Dashboard  
- Provides an organized view of all logged fitness data.  
- Individual activity data is displayed via interactive **buttons/cards**.

### Reset Functionality  
- Enables users to reset data.  
- Upon reset, all activity data is saved into a separate SQLite table.

---

## Code Overview

The app follows best practices in Android development:  
- **ViewModel**: Ensures data persistence and lifecycle awareness.  
- **Repository Pattern**: Decouples the business logic from UI components.  
- **SQLite Integration**: Stores and retrieves activity data efficiently.

Each module/activity code can be found in the repository under the `src/main` directory, with comments for clarity.

---

## Emulator Screenshots

### Home Screen  
[Insert Screenshot]

### Activity Logger  
![image](https://github.com/user-attachments/assets/fd5283a3-39d3-48ee-9a31-92e79dd601c5)


### Dashboard  
[Insert Screenshot]

### Reset Data Confirmation  
[Insert Screenshot]

---

## Conclusion & Future Scope

The **Fitness Tracker App** is a step towards enabling users to maintain their fitness journey effortlessly. In future versions, we aim to:  
- Introduce integration with **Google Fit API**.  
- Add detailed analytics and visualization features.  
- Implement cloud sync to access data across devices.

---

## Project Link 🌐

GitHub Repository: [Fitness Tracker App](https://github.com/your-username/fitness-tracker-app)  

---

Thank you for using the **Fitness Tracker App**! Feel free to raise issues or contribute to the project.  
