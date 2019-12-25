package com.example.editphoto.Interface;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onConstrantChanged(float constrant);
    void onEditStarted();
    void onEditCompleted();

}
