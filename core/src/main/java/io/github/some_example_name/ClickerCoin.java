package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class ClickerCoin {

    BitmapFont font;
    SpriteBatch batch;

    int basketCoin = 0;

    boolean spacePressed = false;


    public void render() {
        batch.begin();
        font.draw(batch, "Move Count: " + basketCoin, 90, 100); // Отображаем текст в левом нижнем углу
        batch.end();
    }

    private void basketCoinUp(int x) {
        basketCoin = x + basketCoin;
    }

    private void counterLogic() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {   // ХАМСТР ХАМСТР КРИМИАНАЛ
            if (!spacePressed) { // Проверяем, была ли клавиша уже нажата
                basketCoinUp(1); // Увеличиваем значение на 1
                spacePressed = true; // Отмечаем, что клавиша нажата
            }
        } else {
            spacePressed = false; // Сбрасываем состояние, если клавиша отпущена
        }
    }


}
