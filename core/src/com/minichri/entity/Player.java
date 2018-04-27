package com.minichri.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.minichri.inventory.Inventory;

public class Player extends TextureObject {

    private boolean hasJumped;
    private boolean isCrouched;
    private float maxVelocity = 100;
    // Inventory singleton
    private static Inventory _inventory;
    public static Inventory getInventory() {
        if (_inventory == null) _inventory = new Inventory();
        return _inventory;
    }

    private static TextureRegion playerTexLeft = new TextureRegion(new Texture("player/player_left.png"),0,0,16,16);

    public Player(World world, Vector2 pos) {
        super(world, pos, GameObject.DEFAULT_DYNAMIC_BODYDEF,GameObject.DEFAULT_DYNAMIC_FIXTUREDEF,playerTexLeft);
        body.setGravityScale(5f);
        body.setLinearDamping(0);


    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        //TEMPORATY

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            body.setLinearVelocity(body.getLinearVelocity().x,8000);
            hasJumped = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
           body.applyLinearImpulse(1000,0,8,11,true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            body.applyLinearImpulse(-1000,0,8,11,true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            System.out.println("Player is crouched");
            System.out.println(body.getMass());
            isCrouched = true;
        }
        else isCrouched = false;


    }

    public Vector2 getBodyPos(){
         return body.getPosition();
    }
}
