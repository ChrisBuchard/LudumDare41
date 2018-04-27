package com.minichri.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum TileType {

    PLAYER("0026FFFF", null, null, false, false),
    WHITE_SPACE("FFFFFFFF", null, null, false, false),
    GROUND("000000FF", "tiles/ground_tiles.png", null, true, false),
    PLATFORM_BLUE("FFFFFFFF", "tiles/platform_blue.png", "items/platform_item_blue.png", false, false),
    PLATFORM_GREEN("FFFFFFFF", "tiles/platform_green.png", "items/platform_item_green.png", false, false),
    PLATFORM_PURPLE("FFFFFFFF", "tiles/platform_purple.png", "items/platform_item_purple.png", false, false),
    RESOURCE_BLUE("00FFFFFF", "tiles/platform_resource_blue.png", null, false, true),
    RESOURCE_GREEN("00FF21FF", "tiles/platform_resource_green.png", null, false, true),
    RESOURCE_PURPLE("FF00DCFF", "tiles/platform_resource_purple.png", null, false, true),
    SPIKES("FF0000FF", null, null, false, false)
    ;

    private Color color;
    private Texture blockTexture;
    private Texture itemTexture;
    private boolean isDirectionalTile;
    private boolean isResourceTile;

    TileType(String color, String pathToTexture, String pathToItemTexture, boolean isDirectionalTile, boolean isResourceTile) {
        this.color = Color.valueOf(color);
        this.isDirectionalTile = isDirectionalTile;
        this.isResourceTile = isResourceTile;

        if (pathToTexture != null) {
            this.blockTexture = new Texture(pathToTexture);
        }
        if (pathToItemTexture != null) {
            this.itemTexture = new Texture(pathToItemTexture);
        }
    }

    /** Takes a color and returns a matching type. Returns null of non matches.
     *  @param color a color to be matched.
     *  @return a tileType based on the color given. Returns null if non matches. */
    public static TileType getTypeFromColor(Color color){

        for (TileType type : TileType.values()) {
            if (type.color.equals(color)) return type;
        }

        return null;
    }

    public Texture getBlockTexture() {
        return blockTexture;
    }

    public Texture getItemTexture() {
        return itemTexture;
    }

    public Color getColor() {
        return color;
    }

    public boolean isDirectionalTile() {
        return isDirectionalTile;
    }

    public boolean isResourceTile() {
        return isResourceTile;
    }

    /** Converts a RESOURCE enum to a PLATFORM enum (used when item is given to inventory) */
    public static TileType convertFromResourceToPlatform(TileType resourceType){

        switch (resourceType){
            case RESOURCE_BLUE: return PLATFORM_BLUE;
            case RESOURCE_GREEN: return PLATFORM_GREEN;
            case RESOURCE_PURPLE: return PLATFORM_PURPLE;
        }

        return null;
    }

    /** An enum describing the tiles placement related to its surroundings */
    public enum TextureDirection {
        LEFT, MIDDLE, RIGHT, UNDER
    }
}
