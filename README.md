# Photoshop Clone

![Mockup Image](Swing-UI-Mockup-Image.png)

## Overview
This Photoshop Clone application is a limited-feature image editing tool designed to mimic the functionality of professional photo editing software. It allows users to open, modify, and save images using a layer-based approach. The application also includes essential tools like brushes, adjustments, undo/redo functionality, and data persistence, ensuring easy editing experience.

## Features

### 1. **Layer-Based Editing**
- **Multiple Layers**: Users can add, delete, and manage multiple layers.
- **Adjustment Layers**: Apply brightness or other adjustments without directly altering the base image.
- **Layer Visibility**: Toggle the visibility of individual layers for precise editing.

### 2. **Brush Tool**
- Draw directly on a selected layer using the brush tool.
- Adjustable brush color.

### 3. **Undo/Redo Functionality** (Unfortunately Unfinished)
- Roll back or restore previous actions for non-destructive editing.
- Accessible via dedicated Undo and Redo buttons.

### 4. **Data Persistence**
- Save your entire project, including layers and adjustments, to a file for later use.
- Open previously saved projects to continue editing from where you left off.

### 5. **Image Export**
- Export your edited image to a single, flattened .png file.
- Includes all visible layers and adjustments.

### 6. **User-Friendly UI**
- Intuitive layout with a toolbar, layers panel, adjustments panel, and canvas.
- Easy-to-access menu options for saving, opening, and exporting files.

## How to Use

### Opening an Image
1. Go to `File > Open Image`.
2. Select a `.png` file from your system. (Note: Only `.png` files are supported by apache commons imaging.)
3. The image will load as a base layer titled "image" in the Layers Panel.

### Adding and Managing Layers
1. Use the **Add Layer** button in the Layers Panel to create new layers.
2. Select a layer to edit name or apply adjustments.
3. Use the visibility toggle in the Layers Panel to hide/show layers.

### Using the Brush Tool
1. Select the **Brush** button in the toolbar.
2. Adjust the brush color using the color palette.
3. Click and drag on the canvas to draw on the **brush** layer.

### Adjustments
1. Use the sliders and buttons in the Adjustments Panel to apply brightness adjustments to the selected **Adjustments Layer**. (The Adjustments Layer must be selected to make changes)
2. Changes will be visible immediately on the canvas.

### Undo/Redo Actions (Unfinished)
1. Click the **Undo** button to reverse your last action.
2. Click the **Redo** button to restore an undone action.
3. Buttons will enable/disable automatically based on available actions.

### Saving and Loading Projects
1. To save your project, go to **File > Save As** and specify a .ser file name.
2. To reopen a saved project, go to **File > Open File** and select a .ser file.
3. Your layers and adjustments will be restored exactly as you left them.

### Exporting Images
1. Go to **File > Export Image**.
2. Choose a destination and filename for the exported .png file.
3. The file will include all visible layers and adjustments.

## Requirements
- !! **OpenCV Dependency** !!: You may need to manually download the `opencv_java490.dll` (included in the root of this directory) file and place it in your `C:\Program Files\Java\jdk-22\bin` directory in order for image adjustments to function correctly. 
- **Operating System**: Windows, macOS, or Linux
- **Java Version**: I used Java 22
- **Memory**: 4GB RAM minimum

## Known Limitations
- Only .png file format is supported for image loading and exporting.
- Performance may degrade with very large images or excessive layers.
- Upon reloading a previously saved serialized file that had adjustments to saturation the image may render incorrectly. One work around is to return the saturation slide to the very middle and the image will render properly. 

## Troubleshooting
- **Brush tool not working after Undo:** Ensure the correct layer is selected in the Layers Panel.
- **Application crashes when opening unsupported file types:** Use only .png files for opening.
- **Undo/Redo buttons are disabled:** Perform an action (e.g., draw or adjust) to enable the buttons.






