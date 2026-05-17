package com.followtheritm.service;

import com.followtheritm.model.SimonColor;

public interface AudioService {
    void playColorTone(SimonColor color);
    void playError();
    void playSuccess();
}
