package com.minichri.Elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.minichri.helpers.GameInfo;
import com.minichri.helpers.TileType;

import java.util.Random;

public class Decoration {

    private TileType tileType;
    private TextureRegion texture;
    private Vector2 pos;
    private float width;
    private float height;


    public Decoration(TileType tileType, Vector2 pos) {
        this.tileType = tileType;

        //Get texture
        TextureRegion[][] textureSheet;
        if(tileType == TileType.MULTIPLEPLANTS)
            textureSheet = TextureRegion.split(tileType.getBlockTexture(), GameInfo.TILE_SIZE * 2, GameInfo.TILE_SIZE);
        else
            textureSheet = TextureRegion.split(tileType.getBlockTexture(), GameInfo.TILE_SIZE, GameInfo.TILE_SIZE);

        Random random = new Random();
        int ranNum = random.nextInt(3);

        this.texture = textureSheet[0][ranNum];


        this.pos = pos;
        this.width = texture.getRegionWidth() * GameInfo.PPM;
        this.height = texture.getRegionHeight() * GameInfo.PPM;
    }

    public void render(SpriteBatch spriteBatch, float delta){
        spriteBatch.draw(texture, pos.x - width/2, pos.y - height/2, width / 2f, height / 2f, width, height, 1, 1, 0);
    }
}
