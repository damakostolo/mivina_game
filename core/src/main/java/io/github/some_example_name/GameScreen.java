package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    final Drop game;
    private final FitViewport viewport;

    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Texture tarakanTexture;

    Sound dropSound;
    Music music;

    Sprite bucketSprite;
    Array<Sprite> dropSprites;
    float dropTimer;

    Rectangle bucketRectangle;
    Rectangle dropRectangle;





    int dropCount = 0; // сколько тараканов или мивин нападало в суп
    int randomInt; // падение тараканов в мивину
    int clickerCoin = 0; // крипта

    boolean spacePressed;

    float timer = 30;  // Устанавливаем таймер на 30 секунд
    boolean timerRunning = true;  // Флаг для отслеживания состояния таймера


    public GameScreen(Drop game) {
        this.game = game;


        viewport = new FitViewport(80,50);

        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        tarakanTexture = new Texture("tarakan.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(10,10);

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        music.setLooping(true);
        music.setVolume(.5f);
        music.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 40f;
        float delta = Gdx.graphics.getDeltaTime(); // retrieve the current delta

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucketSprite.translateX(speed * delta);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucketSprite.translateX(-speed * delta);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            // Проверяем, была ли уже нажата клавиша
            if (!spacePressed) {
                setClickerCoin(1);  // Увеличиваем счетчик на 1
                spacePressed = true;  // Отмечаем, что клавиша нажата
            }
        } else {
            spacePressed = false;  // Сбрасываем состояние, если клавиша отпущена
        }
    }

    private void setClickerCoin(int x) {
        clickerCoin += x;
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Store the bucket size for brevity`
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth-bucketWidth));

        float delta = Gdx.graphics.getDeltaTime(); // retrieve the current delta

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        // Loop through the sprites backwards to prevent out of bounds errors
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i); // Get the sprite from the list
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            randomInt = (int) (Math.random() * 7); // случайное число которое надо постоянно перегенерировать это сверху надо сделать

            dropSprite.translateY(-15f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);
            // if the top of the drop goes below the bottom of the view, remove it
            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if(bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
                dropCount++;
                if (dropCount == 30 || clickerCoin == 30){
                    //game.setScreen(new GameScreen()); // добавить выйграшное окно
                    //dispose();
                }

                if (!timerRunning){
                    //game.setScreen(new GameScreen()); // добавить проигравшее окно
                    //dispose();
                }

            }
        }

        dropTimer += delta; // Adds the current delta to the timer
        if (dropTimer > 1f) { // Check if it has been more than a second
            dropTimer = 0; // Reset the timer
            createDroplet(); // Create the droplet
        }

        if (timerRunning) {
            timer -= delta;  // Вычитаем прошедшее время
            if (timer <= 0) {
                timer = 0;  // Останавливаем таймер, когда он достигнет 0
                timerRunning = false;  // Таймер завершен
            }
        }
    }


    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // задник
        bucketSprite.draw(game.batch);// Ведро

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(game.batch);
        }

        game.font = new BitmapFont();
        game.font.getData().setScale(0.2f); // довольно сносно получилось
        game.font.draw(game.batch,"Food :" + dropCount ,65, 48);
        game.font.draw(game.batch,"ClickerCoin :" + clickerCoin ,60, 45);
        game.font.draw(game.batch, "time: " + (int) timer + " s", 4, 48);

        game.batch.end();
    }

    private void createDroplet() {
        // тут короче мы задаём размеры всему
        float dropWidth = 10;
        float dropHeight = 10;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Создаём сами спрайты
        Sprite dropSprite;// Ну тут закидуем всё в очередь
        if(randomInt > 0) dropSprite = new Sprite(dropTexture);
        else dropSprite = new Sprite(tarakanTexture);

        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);

        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        bucketTexture.dispose();
        dropTexture.dispose();
        music.dispose();
        dropSound.dispose();
    }
}
