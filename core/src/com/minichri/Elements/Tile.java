package com.minichri.Elements;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.minichri.entity.GameObject;
import com.minichri.entity.TextureObject;
import com.minichri.helpers.TileType;

public class Tile extends TextureObject {

    private TileType tileType;

    public Tile(World world, TileType tileType, Vector2 pos) {
        super(world, pos, GameObject.DEFAULT_STATIC_BODYDEF, GameObject.DEFAULT_STATIC_FIXTUREDEF, new TextureRegion(tileType.getBlockTexture()));
        this.tileType = tileType;
        body.setUserData(this);
    }

    public TileType getTileType() {
        return tileType;
    }
}
