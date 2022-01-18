package com.br.gabrielsilva.prismamc.hungergames.api.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.scheduler.BukkitTask;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.worldedit.schematic.object.SchematicBlock;
import com.br.gabrielsilva.prismamc.commons.bukkit.worldedit.schematic.object.SchematicLocation;
import com.br.gabrielsilva.prismamc.commons.bukkit.worldedit.schematic.utils.Region;
import com.br.gabrielsilva.prismamc.commons.bukkit.worldedit.schematic.utils.Vector;
import com.br.gabrielsilva.prismamc.commons.core.utils.system.DateUtils;
import com.br.gabrielsilva.prismamc.hungergames.HungerGames;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class SchematicLoader {

    private final List<SchematicBlock> blockList = new ArrayList<>();
    private final List<SchematicLocation> locationList = new ArrayList<>();
    
    private final Region region;

    private BukkitTask task;

    public SchematicLoader(Region region) {
    	this.region = region;

        World world = region.getWorld();
        Vector min = region.getMinLocation();
        Vector max = region.getMaxLocation();

        //blocks =========================================================================================
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Vector location = new Vector(x, y, z).subtract(min);
                    SchematicBlock schematicBlock = new SchematicBlock(location, block.getTypeId(), block.getData());
                    blockList.add(schematicBlock);
                }
            }
        }
    }

    public SchematicLoader(String nome, File file) throws IOException {
    	FileInputStream stream = new FileInputStream(file);

        NBTTagCompound nbt = NBTCompressedStreamTools.a(stream);

        stream.close();

        short width = nbt.getShort("Width");
        short height = nbt.getShort("Height");
        short length = nbt.getShort("Length");

        int offsetX = nbt.getInt("WEOffsetX");
        int offsetY = nbt.getInt("WEOffsetY");
        int offsetZ = nbt.getInt("WEOffsetZ");
        Vector offset = new Vector(offsetX, offsetY, offsetZ);

        int originX = nbt.getInt("WEOriginX");
        int originY = nbt.getInt("WEOriginY");
        int originZ = nbt.getInt("WEOriginZ");
        Vector origin = new Vector(originX, originY, originZ);

        region = new Region(origin, offset, width, height, length);

        //blocks =========================================================================================
        byte[] blockId = nbt.getByteArray("Blocks");
        byte[] blockData = nbt.getByteArray("Data");

        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length];

        if (nbt.hasKey("AddBlocks")) {
            addId = nbt.getByteArray("AddBlocks");
        }

        for (int index = 0; index < blockId.length; index++) {
            if ((index >> 1) >= addId.length) {
                blocks[index] = (short) (blockId[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                }
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                     int index = y * width * length + z * width + x;
                     SchematicBlock block = new SchematicBlock(new Vector(x, y, z), blocks[index], blockData[index]);
                     blockList.add(block);
                }
            }
        }
        
        //locations =========================================================================================
        NBTTagList locations = nbt.getList("Locations", 8);
        for (int i = 0; i < locations.size(); i++) {
            SchematicLocation location = new SchematicLocation(locations.getString(i));
            locationList.add(location);
        }
    }

    public Region getRegion() {
        return region;
    }

    public void addLocation(SchematicLocation location) {
        locationList.add(location);
    }

    public List<Location> getConvertedLocation(String key, Location pasteLocation) {
        List<Location> result = new ArrayList<>();
        for (SchematicLocation locations : locationList) {
            if (locations.getKey().equals(key)) {
                Vector vector = locations.getLocation().clone().add(new Vector(pasteLocation).add(region.getOffset()).subtract(region.getMinLocation()));
                result.add(vector.toLocation(pasteLocation.getWorld()));
            }
        }
        return result;
    }
    
    public void paste(String nome, Location location, int bpt) {
    	Vector finalLocation = new Vector(location).add(region.getOffset());
        World world = location.getWorld();
        
        pasteBlocks(nome, world, finalLocation, bpt, false);
    }
    
    public void paste(String nome, Location location, int bpt, boolean force) {
    	Vector finalLocation = new Vector(location).add(region.getOffset());
        World world = location.getWorld();
        
        pasteBlocks(nome, world, finalLocation, bpt, force);
    }
    
    //blocks =========================================================================================
    private void pasteBlocks(String nome, World world, Vector pasteLocation, int bpt, boolean force) {
    	HungerGames.console("Colocando a Schematic '"+nome+"' forçado -> " + (force ? "Sim" : "Nao"));
    	long started = System.currentTimeMillis();
    	
    	if (force) {
    		Iterator<SchematicBlock> iterator = blockList.iterator();
    		
    		while (iterator.hasNext()) {
    			SchematicBlock schemBlock = iterator.next();
                Vector finalLocation = schemBlock.getLocation().clone().add(pasteLocation);
                
                setBlock(world, finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ(), schemBlock.getID(), schemBlock.getData());
                
                setAsyncBlock(world, new Location(world, finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ()),
                		schemBlock.getID(), schemBlock.getData());
                
                if (nome.equalsIgnoreCase("coliseu")) {
                	if (schemBlock.getID() == 33) {
                		Block block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
                		SchematicAPI.portaoColiseu.add(block);
                	}
                }
                
                if (nome.equalsIgnoreCase("feast")) {
            		Block block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
            		if ((block.getTypeId() == 54) || (block.getTypeId() == 146)) {
            			HungerGames.getManager().getStructureManager().addChestItems((Chest) block.getLocation().getBlock().getState());
            		}
                }
                iterator.remove();
    		}
            return;
    	}
    	
        task = Bukkit.getScheduler().runTaskTimer(BukkitMain.getInstance(), () -> {
        	
        	Iterator<SchematicBlock> iterator = blockList.iterator();
        	
            for (int i = 0; i < bpt; i++) {
            	if (!iterator.hasNext()) {
            		HungerGames.console("Schematic '"+nome+"' colada em -> " + DateUtils.getElapsed(started));
                    task.cancel();
                    break;
                }

                SchematicBlock schemBlock = iterator.next();
                Vector finalLocation = schemBlock.getLocation().clone().add(pasteLocation);
                setAsyncBlock(world, new Location(world, finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ()),
                		schemBlock.getID(), schemBlock.getData());
                
                if (nome.equalsIgnoreCase("coliseu")) {
                	if (schemBlock.getID() == 33) {
                		Block block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
                		SchematicAPI.portaoColiseu.add(block);
                	}
                }
                
                if (nome.equalsIgnoreCase("feast")) {
            		Block block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
            		SchematicAPI.bFeast.add(block);
                	if (schemBlock.getID() == 116) {
                		SchematicAPI.Enchant.add(block);
                		SchematicAPI.Circulo.add(block);
                		block.setType(Material.AIR);
                		
					  	for (int xx = -6; xx < 6; xx++) {
					         for (int yy = -6; yy < 6; yy++) {
						          for (int zz = -6; zz < 6; zz++) {
						           	   Block b = block.getLocation().clone().add(xx, yy, zz).getBlock();
						           	   SchematicAPI.Circulo.add(b);
						          }
					         }
					  	}
					  	
                	} else if ((block.getTypeId() == 54) || (block.getTypeId() == 146)) {
                		block.setType(Material.AIR);
                		SchematicAPI.Baus.add(block);
                	}
                } 
                if (nome.equalsIgnoreCase("minifeast")) {
            		Block block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
                	if ((block.getTypeId() == 54) || (block.getTypeId() == 146)) {
                		block.setType(Material.CHEST);
                		
                		HungerGames.getManager().getStructureManager().addChestItemsMinifeast((Chest) block.getLocation().getBlock().getState());
                	}
                }
                iterator.remove();
            }
        }, 0L, 1L);
    }

    private void setBlock(World world, int x, int y, int z, int id, byte data) {
        world.getBlockAt(x, y, z).setTypeIdAndData(id, data, false);
    }
    
	public void setAsyncBlock(World world, Location location, int blockId) {
		setAsyncBlock(world, location, blockId, (byte) 0);
	}
	
	public void setAsyncBlock(World world, Location location, int blockId, byte data) {
		setAsyncBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, data);
	}
	
	public void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
		net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		BlockPosition bp = new BlockPosition(x, y, z);
		int i = blockId + (data << 12);
		IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
		chunk.a(bp, ibd);
		w.notify(bp);
	}
}