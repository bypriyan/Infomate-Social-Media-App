# Infomate-Social-Media-App

Infomate is a social media Android application that allows users to connect, share, and communicate with each other. This app provides a variety of features including user authentication, profile creation, post sharing, real-time chatting, group messaging, and group video calls. 

## Features

- **User Authentication**: Users authenticate using Firebase Auth.
- **Profile Creation**: Users can create their profile by entering their name and selecting a profile picture. Images are compressed by 50% to save storage without losing quality.
- **Post Sharing**: Users can upload posts which can be liked, commented on, and shared by others.
- **Shimmer Animation**: Shimmer animation is used to show loading states.
- **Reporting**: Users can report any post if they find it inappropriate.
- **Chatting**: Users can chat with each other, sharing both text and images.
- **User Search**: Users can search for other users within the app.
- **Blocking**: Users can block each other if needed.
- **Group Messaging**: Similar to WhatsApp, users can create groups where multiple users can chat and share text messages.
  - **Admin Controls**: Admins can add or remove users, make other users admin, and delete messages within the group.
- **Group Video Calls**: Users can create group video calls similar to Google Meet or Zoom using Jitsi SDK.

## Technologies Used

- **Java**: For Android app development.
- **PHP**: Backend server-side scripting.
- **Retrofit**: For making network requests.
- **Firebase Realtime Database**: For real-time data synchronization.
- **MySQL**: For database management.
- **Jitsi SDK**: For implementing video call features.
- **Firebase Phone Auth**: For phone number authentication.

## Screenshots

Screenshots of the app will be added here to provide a visual overview of the features and UI.
![WhatsApp Image 2024-06-07 at 5 53 10 PM](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/4bb1db50-1f97-4821-b537-de1c815fd459)
![WhatsApp Image 2024-06-07 at 5 53 09 PM (2)](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/e86fa951-5414-44b5-ae88-dbee90e40d31)
![WhatsApp Image 202![WhatsApp Image 2024-06-07 at 5 53 09 PM](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/01b4f9b0-9a7c-427d-9599-dce25a5ae89b)
4-06-07 at 5 53 09 PM (1)](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/c20419a5-cfc0-4e08-b25d-88903d19762d)
![WhatsApp Image 2024-06-07 at 5 53 08 PM (1)](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/cb2ffb0d-2656-4164-a0f8-e86d12996a0c)
![WhatsApp Image 2024-06-07 at 5 53 08 PM](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/e016d89e-c46d-4561-8ee6-b4c199eca9ad)
![WhatsApp Image 2024-06-07 at 5 53 07 PM (1)](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/c77c75ae-6b65-4ff5-9ace-bd4eb0e95fad)
![WhatsApp Image 2024-06-07 at 5 53 07 PM (2)](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/02f76da8-9c86-4330-a289-e7bda9f05680)
![WhatsApp Image 2024-06-07 at 5 53 07 PM](https://github.com/bypriyan/Info![WhatsApp Image 2024-06-07 at 5 53 06 PM](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/db6124a8-8df3-4cdb-b043-e2987348da94)



## Getting Started

### Prerequisites

- Android Studio
- Firebase account
- PHP server
- MySQL server

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/bypriyan/Infomate-Social-Media-App.git
    cd Infomate-Social-Media-App
    ```

2. Open the project in Android Studio.

3. Set up Firebase for your Android app:
   - Go to the [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Add your Android app to the project
   - Follow the instructions to download the `google-services.json` file and place it in the `app` directory
   - Enable Firebase Authentication (Email/Password and Phone)

4. Set up your PHP backend and MySQL database:
   - Configure your PHP server to handle API requests.
   - Create a MySQL database and import the necessary tables.

5. Update the backend URLs in your Retrofit instance.

6. Build and run the app on your Android device or emulator.

## Contributing

Contributions are welcome! Please fork this repository and submit pull requests for any enhancements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

If you have any questions or suggestions, feel free to reach out to me at [104priyanshu@gmail.com].

---

Thank you for checking out Infomate! We hope you enjoy using the app as much as we enjoyed building it.