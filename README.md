# 2D Floor Planner

A Java-based application for creating and managing 2D floor plans with a modern user interface using FlatLaf.

## Features

- Modern, user-friendly interface with FlatLaf theme
- Secure login system
- Interactive room creation and management
- Drag-and-drop room positioning
- Room type selection (Bedroom, Kitchen, Living Room, etc.)
- Room rotation and modification
- Save and load floor plans
- Grid-based layout for precise positioning
- Collision detection between rooms

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Apache Maven
- Git (optional, for cloning the repository)

## Installation Guide

### Windows

1. Install Java JDK 17 or higher:
   - Download OpenJDK from [Adoptium](https://adoptium.net/)
   - Run the installer
   - Verify installation:
     ```cmd
     java -version
     ```

2. Install Maven:
   - Download [Apache Maven](https://maven.apache.org/download.cgi)
   - Extract to a directory (e.g., `C:\Program Files\Apache\maven`)
   - Add to System Environment Variables:
     - Add `M2_HOME`: `C:\Program Files\Apache\maven`
     - Add to PATH: `%M2_HOME%\bin`
   - Verify installation:
     ```cmd
     mvn -version
     ```

3. Clone or download the project:
   ```cmd
   git clone https://github.com/anshull-saxena/2D_Floor_Planner.git
   cd 2D_Floor_Planner
   ```

### macOS

1. Install Java JDK 17 or higher:
   ```bash
   # Using Homebrew
   brew install openjdk@17

   # Verify installation
   java -version
   ```

2. Install Maven:
   ```bash
   # Using Homebrew
   brew install maven

   # Verify installation
   mvn -version
   ```

3. Clone or download the project:
   ```bash
   git clone https://github.com/anshull-saxena/2D_Floor_Planner.git
   cd 2D_Floor_Planner
   ```

### Linux (Ubuntu/Debian)

1. Install Java JDK 17:
   ```bash
   # Update package list
   sudo apt update

   # Install OpenJDK 17
   sudo apt install openjdk-17-jdk

   # Verify installation
   java -version
   ```

2. Install Maven:
   ```bash
   # Install Maven
   sudo apt install maven

   # Verify installation
   mvn -version
   ```

3. Clone or download the project:
   ```bash
   git clone https://github.com/anshull-saxena/2D_Floor_Planner.git
   cd 2D_Floor_Planner
   ```

### Linux (Fedora/RHEL)

1. Install Java JDK 17:
   ```bash
   # Install OpenJDK 17
   sudo dnf install java-17-openjdk-devel

   # Verify installation
   java -version
   ```

2. Install Maven:
   ```bash
   # Install Maven
   sudo dnf install maven

   # Verify installation
   mvn -version
   ```

3. Clone or download the project:
   ```bash
   git clone https://github.com/anshull-saxena/2D_Floor_Planner.git
   cd 2D_Floor_Planner
   ```

## Building the Project

After installation, build the project:

```bash
# Windows (Command Prompt)
mvn clean install

# macOS/Linux (Terminal)
mvn clean install
```

## Running the Application

1. Start the application:
   ```bash
   # Windows/macOS/Linux
   mvn exec:java
   ```

2. Login credentials:
   - Username: admin
   - Password: admin

## Usage Guide

1. **Login**:
   - Launch the application
   - Enter your credentials
   - Click "Login" or press Enter

2. **Creating Rooms**:
   - Select room type from the "Room Type" button
   - Set dimensions using "Set Dimension"
   - Set position using "Set Position"
   - Click "Add Room" to place the room

3. **Modifying Rooms**:
   - Click on a room to select it
   - Use "Properties" to view room details
   - Use "Modify" to:
     - Rotate the room
     - Remove the room
   - Drag rooms to reposition them

4. **Saving/Loading**:
   - Click "Download" to save your floor plan
   - Click "Open File" to load an existing plan
   - Files are saved in .2ds format

5. **Additional Features**:
   - Grid system for precise alignment
   - Automatic door placement between adjacent rooms
   - Collision detection prevents room overlap

## Project Structure

- `Login.java`: Handles user authentication
- `RoomPlan2.java`: Main application logic and UI
- `pom.xml`: Maven project configuration

## Development

To modify the application:

1. Import the project into your IDE (Eclipse, IntelliJ IDEA, or VS Code)
2. Ensure Maven dependencies are resolved
3. Run the main class `Login.java`

## Troubleshooting

### Windows
1. If Java is not recognized:
   - Check System Environment Variables
   - Add JAVA_HOME to System Variables
   - Add %JAVA_HOME%\bin to PATH

2. If Maven is not recognized:
   - Verify M2_HOME in System Variables
   - Verify %M2_HOME%\bin in PATH
   - Restart Command Prompt

### macOS
1. If Maven is not found:
   ```bash
   brew install maven
   ```

2. If Java is not found:
   ```bash
   brew install openjdk@17
   ```

### Linux
1. If Java is not found:
   ```bash
   # Ubuntu/Debian
   sudo apt install openjdk-17-jdk

   # Fedora/RHEL
   sudo dnf install java-17-openjdk-devel
   ```

2. If Maven is not found:
   ```bash
   # Ubuntu/Debian
   sudo apt install maven

   # Fedora/RHEL
   sudo dnf install maven
   ```

### General Issues
1. If dependencies fail to download:
   ```bash
   mvn clean install -U
   ```

2. If the UI looks incorrect:
   - Ensure FlatLaf dependencies are properly included
   - Check Java version compatibility
   - Try cleaning and rebuilding:
     ```bash
     mvn clean
     mvn compile
     mvn exec:java
     ```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

Feel free to reach out with any questions or feedback regarding the **2D Floor Planner** project!
f20221041@hyderabad.bits-pilani.ac.in
