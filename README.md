# INF2007_MAD - Medication Reminder App
Welcome to onemoregame Mobile App project! This repository contains our project, where our group develop a mobile application that streamlines medication management, ensuring users adhere to their prescribed schedules effortlessly. Targeting individuals with chronic conditions, elderly users, or those with complex medication routines, the application leverages modern technology to track doses, set reminders, and locate pharmacies, enhancing convenience and healthcare outcomes.

## Getting Started

### Features

- Medication reminders that send timely notifications based on customized schedules
- GPS-based pharmacy locator that shows nearby pharmacies with navigation options
- OCR prescription scanning that extracts medication details from images to reduce manual entry
- Medication management dashboard that displays and organizes all medication information
- Cloud database integration for storing user information and medication details

### Project Structure

```bash
├── MainActivity.kt                # Application Starting Point
│
├── Models                         # Data Models
│   ├── MedicationEntity.kt        # Represents a medication item
│   ├── Pharmacy.kt                # Represents a pharmacy entity
│
├── Views                          # UI Screens & Fragments
│   ├── Activities                 # Contains main screens (Activities)
│   │   ├── Homepage.kt            # Home screen
│   │   ├── MedicationActivity.kt  # Medication reminder screen
│   │   ├── PharmacyFinderActivity.kt  # Pharmacy finder screen
│   │   ├── Registration.kt        # User registration screen
│   │   ├── ScanPrescriptionActivity.kt  # Prescription scanning screen
│   │   ├── Settings.kt            # Settings screen
│   │
│   ├── Fragments                  # UI Fragments
│   │   ├── MedicationDialogFragment.kt  # Medication-related dialog
│   │   ├── MedicationListFragment.kt    # Medication list fragment
│   │   ├── NewScanMedListFragment.kt    # Scanned medication list
│   │   ├── PharmacyFinderFragment.kt    # Pharmacy finder UI
│   │
│   ├── Adapters                   
│   │   ├── MedicationAdapter.kt   # Displaying medication items
│   │   ├── PharmacyAdapter.kt     # Displaying pharmacy items
│
├── ViewModels                     # MVVM ViewModels
│   ├── LoginViewModel.kt          # Handles login UI logic
│   ├── MedicationViewModel.kt     # Manages medication list UI state
│   ├── PharmacyFinderViewModel.kt # Handles pharmacy finder logic
│   ├── RegistrationViewModel.kt   # Manages user registration process
│   ├── ScanMedsViewModel.kt       # Handles prescription scanning logic
│   ├── SettingsViewModel.kt       # Manages app settings UI state
│
├── Database                       # Database Access
│   ├── DBConnection.kt            # Database connection helper
│
├── Repository                    
│   ├── LoginDataManager.kt        # Handles login-related DB queries
│   ├── MedicationDataManager.kt   # Handles medication-related DB operations
│   ├── RegistrationDataManager.kt # Handles user registration DB operations
│   ├── SettingsDataManager.kt     # Handles app settings DB operations       
│
├── Utils                          # Utility Classes
│   ├── NotificationReceiver.kt    # Handles scheduled notifications
│   ├── NotificationUtils.kt       # Helper functions for notifications
│   ├── Session.kt                 # Manages user session

```

### Database Setup
- Website: https://www.mywindowshosting.com/
- Username: INF2007MAD
- Password: INF2007mad
![step1.png](assets%2Fstep1.png)
![step2.png](assets%2Fstep2.png)
![step3.png](assets%2Fstep3.png)
![step4.png](assets%2Fstep4.png)
![step5.png](assets%2Fstep5.png)
![step6.png](assets%2Fstep6.png)

### Project Setup
**Prerequisites**
1. 

**Steps**
1. 