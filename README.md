# Khidmatgar

**Khidmatgar** is an Android application developed in Kotlin that enables users to book service providers for various home chores such as cooking, cleaning, laundry, and more. The app offers functionalities like account creation, profile management, service booking, bookmarking service providers, searching, viewing booking history, and canceling bookings. The [(backend)](https://github.com/yeshalkhan/khidmatgar-backend) is built using .NET with an API leveraging Entity Framework for database interactions.

## Features

- **User Authentication**: Create and manage user accounts.
- **Profile Management**: Edit and update user profiles.
- **Service Booking**: Book service providers for various home chores.
- **Bookmarking**: Save favorite service providers for future reference.
- **Search Functionality**: Search for service providers based on services offered.
- **Service Provider Profiles**: View detailed profiles of service providers.
- **Booking History**: Access and manage past bookings.
- **Booking Cancellation**: Cancel existing bookings when necessary.

## Technical Overview

### Frontend

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Key Components**:
  - **RecyclerView**: Utilized for displaying lists of service providers and bookings.
  - **Firebase Authentication**: Used for user authentication (sign-in, sign-up, and session management).
  - **Fragments**: Employed for modular UI components.
  - **Retrofit**: Used for handling API requests and responses.
  - **DataStore**: Implemented for local storage of bookmarks and user preferences.
  - **Adapters**: Custom adapters for binding data to RecyclerViews.

### Backend

- **Framework**: .NET
- **API**: RESTful API developed with Entity Framework for database operations.

