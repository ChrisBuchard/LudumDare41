package com.minichri.entity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.minichri.Elements.Tile;
import com.minichri.KeyboardController;
import com.minichri.World.GameMap;
import com.minichri.helpers.GameInfo;
import com.minichri.helpers.TileType;
import com.minichri.inventory.Inventory;
import com.minichri.physics.ContactManager;
import com.minichri.screens.GameScreen;

import java.util.ArrayList;


public class Player extends TextureObject {

    private static final int PIXEL_WIDTH = 16;
    private static final int PIXEL_HEIGHT = 21;

    public static final float WIDTH = 0.56f;
    public static final float HEIGHT = 1.35f;

    private static final float FEET_WIDTH = WIDTH - 0.05f;
    private static final float FEET_HEIGHT = 0.2f;
    private static final float FEET_Y_OFFSET = -.85f;
    private static final float MAX_X_VEL = 6f;
    private static final float JUMP_FORCE = 11.4f;
    private static final float JUMP_FORCE_IN_AIR = 9f;
    private static final float WALK_SPEED = 6f;
    private static final float AIR_WALK_FORCE = 0.3f;

    private ArrayList<RenderableObject> playerTiles;
    private ArrayList<RenderableObject> queue;

    private static TextureRegion playerTexLeft = new TextureRegion(new Texture("player/player_left.png"), 0, 0, PIXEL_WIDTH, PIXEL_HEIGHT);
    private static TextureRegion playerTexRight = new TextureRegion(new Texture("player/player_right.png"), 0, 0, PIXEL_WIDTH, PIXEL_HEIGHT);
    private static TextureRegion playerShip = new TextureRegion(new Texture("escape_pod.png"), 0, 0, PIXEL_WIDTH*2, PIXEL_HEIGHT*2);

    // Inventory singleton
    private static Inventory _inventory;
    public static Inventory getInventory() {
        if (_inventory == null) _inventory = new Inventory();
        return _inventory;
    }

    private World world;
    private boolean isMidAir = false;
    private boolean isCrouched = false;
    private boolean hasJumped = false;
    private float maxRange = 5f;
    private float minRange = 1.6f;
    private Vector2 placeVector = new Vector2(0,0);
    private Texture preview;

    private boolean spawning;
    private boolean isDead = false;
    private float deathTimer = 0;
    //Spawning timer
    private float timeSeconds;
    private float spawntimer =2;
    private Vector2 startPosition = new Vector2(0,0);


    private Body feet;

    public Player(World world, Vector2 pos) {
        super(world, pos, GameObject.DEFAULT_DYNAMIC_BODYDEF, GameObject.DEFAULT_DYNAMIC_FIXTUREDEF, playerTexLeft);

        playerTiles = new ArrayList<>();
        queue = new ArrayList<>();
        spawning = true;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(FEET_WIDTH/2f, FEET_HEIGHT/2f);

        this.world = world;
        FixtureDef feetDef = new FixtureDef();
        feetDef.shape = shape;
        feetDef.density = 0;
        feetDef.friction = 1;
        feetDef.restitution = 0;
        feetDef.isSensor = false;
        feet = new GameObject(world, new Vector2(pos.x, pos.y + FEET_Y_OFFSET), GameObject.DEFAULT_DYNAMIC_BODYDEF,feetDef).getBody();
        feet.setUserData(ContactManager.FEET);
        feet.setGravityScale(0);
        body.setLinearDamping(0);
        body.setUserData(this);
    }

    public void render(GameMap map, World world, Vector3 mousePos, KeyboardController controller, SpriteBatch batch, float delta) {


        //adds Player spawned tiles to the array
        if (queue.size()>0)playerTiles.addAll(queue);
            queue.removeAll(queue);

        for(RenderableObject renderableObject : playerTiles)
                renderableObject.render(batch, delta);

        timeSeconds+=delta;

        if (timeSeconds<spawntimer) {
            startPosition = new Vector2(body.getPosition().x - 2f, body.getPosition().y - 2f);
        }
         //Draws player Ship. Needs to be here to be drawn in the right layerqqq
            batch.draw(playerShip, startPosition.x, startPosition.y, 4, 4);
        if (timeSeconds>spawntimer) {
            if (!isDead) {
                movement(map, controller, batch, delta);
                blockPlacing(batch, map, controller, mousePos);
            } else {
                resolveDeath(batch, delta);
            }

            super.render(batch, delta);
        }

    }

    private void blockPlacing(SpriteBatch batch, GameMap map, KeyboardController controller, Vector3 mousePos) {
        //Spawn blocks at the click
        if (getInventory().getSelectedItem()!=null){
            placeVector.x = Math.round(mousePos.x);
            placeVector.y = Math.round(mousePos.y);
            float distance = new Vector2(placeVector).sub(body.getPosition()).len();
            if (distance<maxRange && distance>minRange){
                preview = getInventory().getSelectedItem().getType().getBlockTexture();
                batch.draw(getInventory().getSelectedItem().getType().getBlockTexture(),placeVector.x-0.5f,placeVector.y-0.5f,1, 1);
                if (controller.leftClick){
                    if (!map.isTileOcccipied((int)placeVector.x, (int)placeVector.y)) {
                        TileType type = getInventory().getSelectedItem().getType();
                        map.setTile(type, placeVector);
                        queue.add(new Tile(world, type, placeVector));
                        getInventory().remove(getInventory().getSelectedSlot());
                    }
                }
            }
        }
    }

    private void movement(GameMap map, KeyboardController controller, SpriteBatch batch, float delta) {
        Vector2 vel = body.getLinearVelocity();

        isMidAir = !(ContactManager.feetCollisions > 0 && Math.abs(vel.y) <= 1e-2);
     //avoid those things

        if (!isMidAir) hasJumped = false;
        if (!hasJumped && (controller.w || controller.space)) {
            vel.y = isMidAir ? JUMP_FORCE_IN_AIR : JUMP_FORCE;
            isMidAir = true;
            hasJumped = true;
        }

        // Movement
        int dir = controller.d ? 1 : controller.a ? -1 : 0;
        if (!isMidAir) {
            // Grounded
            vel.x = WALK_SPEED * dir;
        } else {
            // Mid air
            vel.add(AIR_WALK_FORCE * dir, 0);
        }

        if (controller.s) isCrouched = true;
        else isCrouched = false;

        // Restrict vel x
        float restrictVelX = Math.min(Math.max(-MAX_X_VEL, vel.x), MAX_X_VEL);
        vel.x = restrictVelX;

        // Apply new vel
        body.setLinearVelocity(vel);

        // Move feet
        feet.setTransform(new Vector2(body.getPosition()).add(0, FEET_Y_OFFSET), 0);

        updateTexture(dir);
        super.render(batch, delta);

    }

    private void updateTexture(int moveDirection) {
        // Changes the player texture based on movement
        if (moveDirection < 0)
            texture = playerTexLeft;
        else if (moveDirection > 0)
            texture = playerTexRight;
    }

    public void resolveDeath(SpriteBatch batch, float delta) {
        deathTimer += delta;
        if (deathTimer < 0.5f) batch.setColor(1, 0, 0, 1);
        else if (delta < 1f) batch.setColor(0, 0, 0, 1);
        else {
            isDead = false;
        }
    }

    public void kill() {
        isDead = true;
        deathTimer = 0;
    }

    public Vector2 getBodyPos(){
         return body.getPosition();
    }
}
