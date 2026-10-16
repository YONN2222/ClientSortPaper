package com.yonn2222.clientsortpaper.bedrock;

import org.bukkit.entity.Player;

public interface BedrockMenu {
    boolean isBedrock(Player player);

    void open(Player player);
}
