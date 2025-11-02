package com.gotze.spellcasting.pickaxe.ability.todo;

//public class SwipeAbility extends Ability implements BlockBreaker {
//
//    public SwipeAbility() {
//        super(AbilityType.SWIPE);
//    }
//
//    @Override
//    public void activateAbility(Player player, PickaxeData pickaxeData) {
//        player.sendMessage("Swipe ability activated!");
//
//        World world = player.getWorld();
//        Location spawnLocation = player.getEyeLocation();
//
//        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);
//        itemDisplay.setBrightness(new Display.Brightness(15, 15));
//        itemDisplay.setTransformationMatrix(new Matrix4f()
//                .rotateZ((float) Math.toRadians(0))
//                .rotateX((float) Math.toRadians(90f))
//                .scale(7.5f, 7.5f, 1f)
//        );
//
//        new BukkitRunnable() {
//            int ticks = 0;
//            @Override
//            public void run() {
//                String spriteName = switch (ticks) {
//                    case 0 -> "crescent_frontside04";
//
//                    case 1 -> "crescent_frontside05";
//
//                    case 2 -> "crescent_frontside06";
//                    case 3 -> "crescent_frontside06";
//
//                    case 4 -> "crescent_frontside07";
//                    case 5 -> "crescent_frontside07";
//
//
//                    default -> "cancel";
//                };
//
//                if (spriteName.equals("cancel")) {
//                    this.cancel();
//                    itemDisplay.remove();
//                    player.sendMessage("Swipe ability deactivated");
//                }
//
//                itemDisplay.setItemStack(ItemStack.of(Material.PAPER));
//
//                ItemStack itemStack = itemDisplay.getItemStack();
//                itemStack.editMeta(itemMeta -> itemMeta.setItemModel(NamespacedKey.minecraft(spriteName)));
//                itemDisplay.setItemStack(itemStack);
//
//
//                ticks++;
//                if (ticks >= 20) {
//                    this.cancel();
//                    itemDisplay.remove();
//                    player.sendMessage("Swipe ability deactivated");
//                }
//            }
//
//        }.runTaskTimer(Spellcasting.getPlugin(), 0, 1);
//
//
//
//
//        double reachDistance = 4.5;
//        BlockFace playerFacing = player.getFacing();
//        Location eyeLocation = player.getEyeLocation();
//
//        List<Block> blocksInLineOfSight = player.getLineOfSight(null, 5);
//        List<Block> blocksToBreak = new ArrayList<>(blocksInLineOfSight);
//        int testInt = 0;
//        for (int i = 0; i < 2; i++) {
//            testInt += 2;
//            for (Block block : blocksInLineOfSight) {
//                Location blockLocation = block.getLocation();
//
//                double nearestX = Math.max(blockLocation.getX(), Math.min(eyeLocation.getX(), blockLocation.getX() + 1));
//                double nearestY = Math.max(blockLocation.getY(), Math.min(eyeLocation.getY(), blockLocation.getY() + 1));
//                double nearestZ = Math.max(blockLocation.getZ(), Math.min(eyeLocation.getZ(), blockLocation.getZ() + 1));
//
//                double distanceToNearestPoint = eyeLocation.distanceSquared(new Location(blockLocation.getWorld(), nearestX, nearestY, nearestZ));
//                if (distanceToNearestPoint > reachDistance * reachDistance) continue;
//
//                blocksToBreak.addAll(switch (testInt) {
//                    case 0, 1 -> BlockUtils.getVerticalBlocks(block);
//                    case 2 -> BlockUtils.getPositiveDiagonalBlocks(block, playerFacing, 1);
//                    case 3 -> BlockUtils.getNegativeDiagonalBlocks(block, playerFacing, 1);
//                    case 4, 5 -> BlockUtils.getHorizontalBlocks(block, playerFacing);
//                    default -> throw new IllegalStateException("Unexpected value: " + testInt);
//                });
//            }
//        }
//        breakBlocks(player, blocksToBreak, pickaxeData, false);
//    }
//}