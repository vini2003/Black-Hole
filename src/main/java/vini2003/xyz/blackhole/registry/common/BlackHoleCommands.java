package vini2003.xyz.blackhole.registry.common;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vini2003.xyz.blackhole.common.components.BlackHoleComponent;
import vini2003.xyz.blackhole.common.config.BlackHoleConfig;
import vini2003.xyz.blackhole.registry.client.BlackHoleNetworking;

public class BlackHoleCommands {
	private static int spawnBlackHole(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Vec3d pos = context.getSource().getPlayer().getPos();
		
		World world = context.getSource().getWorld();
		
		if (BlackHoleComponents.BLACK_HOLES.get(world).getBlackHoles().isEmpty()) {
			BlackHoleComponent blackHole = new BlackHoleComponent(world);
			blackHole.setPos(pos);
			blackHole.setSize(1F);
			
			BlackHoleComponents.BLACK_HOLES.get(world).getBlackHoles().add(blackHole);
			BlackHoleComponents.BLACK_HOLES.sync(world);
			
			context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.spawn.success").formatted(Formatting.GREEN), true);
		} else {
			context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.spawn.failure").formatted(Formatting.RED), true);
		}
		
		return 1;
	}
	
	private static int kill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		World world = context.getSource().getWorld();
		
		if (!BlackHoleComponents.BLACK_HOLES.get(world).getBlackHoles().isEmpty()) {
			BlackHoleComponents.BLACK_HOLES.get(world).getBlackHoles().clear();
			BlackHoleComponents.BLACK_HOLES.sync(world);
			
			context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
				ServerPlayNetworking.send(player, BlackHoleNetworking.KILL_PACKET, new PacketByteBuf(Unpooled.buffer()));
			});
			
			context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.kill.success").formatted(Formatting.GREEN), true);
		} else {
			context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.kill.failure").formatted(Formatting.RED), true);
		}
		
		return 1;
	}
	
	private static int followSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		float followSpeed = IntegerArgumentType.getInteger(context, "followSpeed") * BlackHoleConfig.defaultFollow;
		
		int previousSpeed = (int) (BlackHoleConfig.cache.followSpeed / BlackHoleConfig.defaultFollow);
		
		BlackHoleConfig.cache.followSpeed = followSpeed;
		
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeFloat(followSpeed);
		
		context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
			ServerPlayNetworking.send(player, BlackHoleNetworking.FOLLOW_SPEED_PACKET, buf);
		});
		
		context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.followSpeed", previousSpeed, IntegerArgumentType.getInteger(context, "followSpeed")).formatted(Formatting.GREEN), true);
		
		return 1;
	}
	
	private static int growSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		float growSpeed = IntegerArgumentType.getInteger(context, "growSpeed") * BlackHoleConfig.defaultGrow;
		
		int previousSpeed = (int) (BlackHoleConfig.cache.growSpeed / BlackHoleConfig.defaultGrow);
		
		BlackHoleConfig.cache.growSpeed = growSpeed;
		
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeFloat(growSpeed);
		
		context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
			ServerPlayNetworking.send(player, BlackHoleNetworking.GROW_SPEED_PACKET, buf);
		});
		
		context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.growSpeed", previousSpeed, IntegerArgumentType.getInteger(context, "growSpeed")).formatted(Formatting.GREEN), true);
		
		return 1;
	}
	
	private static int pullSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		float pullSpeed = IntegerArgumentType.getInteger(context, "pullSpeed") * BlackHoleConfig.defaultPull;
		
		int previousSpeed = (int) (BlackHoleConfig.cache.pullSpeed / BlackHoleConfig.defaultPull);
		
		BlackHoleConfig.cache.pullSpeed = pullSpeed;
		
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeFloat(pullSpeed);
		
		context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
			ServerPlayNetworking.send(player, BlackHoleNetworking.PULL_SPEED_PACKET, buf);
		});
		
		context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.pullSpeed", previousSpeed, IntegerArgumentType.getInteger(context, "pullSpeed")).formatted(Formatting.GREEN), true);
		
		return 1;
	}
	
	private static int damage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		boolean damage = BoolArgumentType.getBool(context, "damage");
		
		boolean previousDamage = BlackHoleConfig.cache.damage;
		
		BlackHoleConfig.cache.damage = damage;
		
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(damage);
		
		context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
			ServerPlayNetworking.send(player, BlackHoleNetworking.DAMAGE_PACKET, buf);
		});
		
		context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.damage." + (damage ? "enable" : "disable"), previousDamage, damage).formatted(Formatting.GREEN), true);
		
		return 1;
	}
	
	private static int pause(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		BlackHoleConfig.cache.follow = false;
		BlackHoleConfig.cache.pull = false;
		BlackHoleConfig.cache.grow = false;
		BlackHoleConfig.cache.damage = false;
		
		context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
			ServerPlayNetworking.send(player, BlackHoleNetworking.PAUSE_PACKET, new PacketByteBuf(Unpooled.buffer()));
		});
		
		context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.pause").formatted(Formatting.GREEN), true);
		
		return 1;
	}
	
	private static int resume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		BlackHoleConfig.cache.follow = true;
		BlackHoleConfig.cache.pull = true;
		BlackHoleConfig.cache.grow = true;
		BlackHoleConfig.cache.damage = true;
		
		context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> {
			ServerPlayNetworking.send(player, BlackHoleNetworking.RESUME_PACKET, new PacketByteBuf(Unpooled.buffer()));
		});
		
		context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.blackhole.resume").formatted(Formatting.GREEN), true);
		
		return 1;
	}
	
	public static void initialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			LiteralCommandNode<ServerCommandSource> blackHoleRoot = CommandManager.literal("blackhole").build();
			
			LiteralCommandNode<ServerCommandSource> blackHoleSpawn =
					CommandManager.literal("spawn")
							.requires((source) -> source.hasPermissionLevel(2))
							.executes(BlackHoleCommands::spawnBlackHole)
							.build();
			
			LiteralCommandNode<ServerCommandSource> blackHoleKill =
					CommandManager.literal("kill")
							.requires((source) -> source.hasPermissionLevel(2))
							.executes(BlackHoleCommands::kill)
							.build();
			
			LiteralCommandNode<ServerCommandSource> blackHolePull =
					CommandManager.literal("pull")
							.requires((source) -> source.hasPermissionLevel(2))
							.then(
									CommandManager.argument("pullSpeed", IntegerArgumentType.integer())
											.executes(BlackHoleCommands::pullSpeed)
											.build()
							).build();
			
			LiteralCommandNode<ServerCommandSource> blackHoleGrow =
					CommandManager.literal("grow")
							.requires((source) -> source.hasPermissionLevel(2))
							.then(
									CommandManager.argument("growSpeed", IntegerArgumentType.integer())
											.executes(BlackHoleCommands::growSpeed)
											.build()
							).build();
			
			LiteralCommandNode<ServerCommandSource> blackHoleFollow =
					CommandManager.literal("follow")
							.requires((source) -> source.hasPermissionLevel(2))
							.then(
									CommandManager.argument("followSpeed", IntegerArgumentType.integer())
											.executes(BlackHoleCommands::followSpeed)
											.build()
							).build();
			
			LiteralCommandNode<ServerCommandSource> blackHoleDamage =
					CommandManager.literal("damage")
							.requires((source) -> source.hasPermissionLevel(2))
							.then(
									CommandManager.argument("damage", BoolArgumentType.bool())
											.executes(BlackHoleCommands::damage)
											.build()
							).build();
			
			LiteralCommandNode<ServerCommandSource> blackHolePause =
					CommandManager.literal("pause")
							.requires((source) -> source.hasPermissionLevel(2))
							.executes(BlackHoleCommands::pause)
							.build();
			
			LiteralCommandNode<ServerCommandSource> blackHoleResume =
					CommandManager.literal("resume")
							.requires((source) -> source.hasPermissionLevel(2))
							.executes(BlackHoleCommands::resume)
							.build();
			
			
			blackHoleRoot.addChild(blackHoleSpawn);
			blackHoleRoot.addChild(blackHoleKill);
			blackHoleRoot.addChild(blackHolePull);
			blackHoleRoot.addChild(blackHoleGrow);
			blackHoleRoot.addChild(blackHoleFollow);
			blackHoleRoot.addChild(blackHoleDamage);
			blackHoleRoot.addChild(blackHolePause);
			blackHoleRoot.addChild(blackHoleResume);
			
			dispatcher.getRoot().addChild(blackHoleRoot);
		});
	}
}
