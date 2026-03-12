# UStitchApp

**Desktop application for interactive embroidery learning**

## Description
**Final Project** || UStitch is a **standalone desktop app** designed to help users learn embroidery interactively. Built with **JavaFX**, it integrates a small **image generator** and stores user progress using **H2 Database**. 

## Features
- Interactive embroidery lessons
- AI-based pattern/image generation
- Save user progress in H2 database
- User-friendly interface with JavaFX

## Tech Stack
- Java 17  
- JavaFX  
- H2 Database  
- TensorFlow (image generator) linked through the pom file as an independent project
-** UStitchApp.exe file generated to run and install the app (Not included here due to size)**



## How to Run
1. Clone the repository:  
   ```bash
   git clone https://github.com/YOUR_USERNAME/UStitch.git
  
## License
This project is licensed under the Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0). 
Commercial use is not allowed. See the LICENSE file for full details.


---

### **2️⃣ UStitchApp README snippet linking to TensorModel**

```markdown
## AI Pattern Generator
The AI pattern generator uses the **TensorModel** project for image-to-image generation.  
TensorModel is an independent module, linked through Maven (`pom.xml`), and can also be reused in other projects.  

Repository: [TensorModel](https://github.com/YOUR_USERNAME/TensorModule) 

