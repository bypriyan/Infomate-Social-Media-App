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
- **Retrofit**: For making network requests.
- **Firebase Realtime Database**: For real-time data synchronization.
- **Jitsi SDK**: For implementing video call features.
- **Firebase Phone Auth**: For phone number authentication.

## Screenshots

Screenshots of the app will be added here to provide a visual overview of the features and UI.
![ss](https://github.com/bypriyan/Infomate-Social-Media-App/assets/86232180/02d7e97b-1102-4cdd-b961-e7f2d192887b)




## Getting Started

### Prerequisites

- Android Studio
- Firebase account

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
