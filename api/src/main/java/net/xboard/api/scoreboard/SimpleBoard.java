package net.xboard.api.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleBoard {
	private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(8);
	private static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
		 .map(Object::toString)
		 .toArray(String[]::new);
	private static final SimpleBoard.VersionType VERSION_TYPE;
	// Packets and components
	private static final Class<?> CHAT_COMPONENT_CLASS;
	private static final Class<?> CHAT_FORMAT_ENUM;
	private static final Object EMPTY_MESSAGE;
	private static final Object RESET_FORMATTING;
	private static final MethodHandle MESSAGE_FROM_STRING;
	private static final MethodHandle PLAYER_CONNECTION;
	private static final MethodHandle SEND_PACKET;
	private static final MethodHandle PLAYER_GET_HANDLE;
	// Scoreboard packets
	private static final SimpleBoardReflection.PacketConstructor PACKET_SB_OBJ;
	private static final SimpleBoardReflection.PacketConstructor PACKET_SB_DISPLAY_OBJ;
	private static final SimpleBoardReflection.PacketConstructor PACKET_SB_SCORE;
	private static final SimpleBoardReflection.PacketConstructor PACKET_SB_TEAM;
	private static final SimpleBoardReflection.PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;
	// Scoreboard enums
	private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
	private static final Class<?> ENUM_SB_ACTION;
	private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
	private static final Object ENUM_SB_ACTION_CHANGE;
	private static final Object ENUM_SB_ACTION_REMOVE;
	
	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			
			if (SimpleBoardReflection.isRepackaged()) VERSION_TYPE = SimpleBoard.VersionType.V1_17;
			else if (SimpleBoardReflection.nmsOptionalClass(null, "ScoreboardServer$Action").isPresent()) VERSION_TYPE = SimpleBoard.VersionType.V1_13;
			else if (SimpleBoardReflection.nmsOptionalClass(null, "IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent()) VERSION_TYPE = SimpleBoard.VersionType.V1_8;
			else VERSION_TYPE = SimpleBoard.VersionType.V1_7;
			
			String gameProtocolPackage = "network.protocol.game";
			Class<?> craftPlayerClass = SimpleBoardReflection.obcClass("entity.CraftPlayer");
			Class<?> craftChatMessageClass = SimpleBoardReflection.obcClass("util.CraftChatMessage");
			Class<?> entityPlayerClass = SimpleBoardReflection.nmsClass("server.level", "EntityPlayer");
			Class<?> playerConnectionClass = SimpleBoardReflection.nmsClass("server.network", "PlayerConnection");
			Class<?> packetClass = SimpleBoardReflection.nmsClass("network.protocol", "Packet");
			Class<?> packetSbObjClass = SimpleBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective");
			Class<?> packetSbDisplayObjClass = SimpleBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective");
			Class<?> packetSbScoreClass = SimpleBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore");
			Class<?> packetSbTeamClass = SimpleBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam");
			Class<?> sbTeamClass = SimpleBoard.VersionType.V1_17.isHigherOrEqual()
				 ? SimpleBoardReflection.innerClass(packetSbTeamClass, innerClass -> !innerClass.isEnum()) : null;
			Field playerConnectionField = Arrays.stream(entityPlayerClass.getFields())
				 .filter(field -> field.getType().isAssignableFrom(playerConnectionClass))
				 .findFirst().orElseThrow(NoSuchFieldException::new);
			Method sendPacketMethod = Arrays.stream(playerConnectionClass.getMethods())
				 .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == packetClass)
				 .findFirst().orElseThrow(NoSuchMethodException::new);
			
			MESSAGE_FROM_STRING = lookup.unreflect(craftChatMessageClass.getMethod("fromString", String.class));
			CHAT_COMPONENT_CLASS = SimpleBoardReflection.nmsClass("network.chat", "IChatBaseComponent");
			CHAT_FORMAT_ENUM = SimpleBoardReflection.nmsClass(null, "EnumChatFormat");
			EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0);
			RESET_FORMATTING = SimpleBoardReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21);
			PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass));
			PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField);
			SEND_PACKET = lookup.unreflect(sendPacketMethod);
			PACKET_SB_OBJ = SimpleBoardReflection.findPacketConstructor(packetSbObjClass, lookup);
			PACKET_SB_DISPLAY_OBJ = SimpleBoardReflection.findPacketConstructor(packetSbDisplayObjClass, lookup);
			PACKET_SB_SCORE = SimpleBoardReflection.findPacketConstructor(packetSbScoreClass, lookup);
			PACKET_SB_TEAM = SimpleBoardReflection.findPacketConstructor(packetSbTeamClass, lookup);
			PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass == null
				 ? null
				 : SimpleBoardReflection.findPacketConstructor(sbTeamClass, lookup);
			
			for (Class<?> clazz : Arrays.asList(packetSbObjClass, packetSbDisplayObjClass, packetSbScoreClass, packetSbTeamClass, sbTeamClass)) {
				if (clazz == null) continue;
				
				Field[] fields = Arrays.stream(clazz.getDeclaredFields())
					 .filter(field -> !Modifier.isStatic(field.getModifiers()))
					 .toArray(Field[]::new);
				for (Field field : fields) field.setAccessible(true);
				
				PACKETS.put(clazz, fields);
			}
			
			if (SimpleBoard.VersionType.V1_8.isHigherOrEqual()) {
				ENUM_SB_HEALTH_DISPLAY = SimpleBoardReflection.nmsClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay");
				ENUM_SB_ACTION = SimpleBoardReflection.nmsClass("server", SimpleBoard.VersionType.V1_13.isHigherOrEqual()
					 ? "ScoreboardServer$Action"
					 : "PacketPlayOutScoreboardScore$EnumScoreboardAction");
				ENUM_SB_HEALTH_DISPLAY_INTEGER = SimpleBoardReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0);
				ENUM_SB_ACTION_CHANGE = SimpleBoardReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0);
				ENUM_SB_ACTION_REMOVE = SimpleBoardReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1);
			} else {
				ENUM_SB_HEALTH_DISPLAY = null;
				ENUM_SB_ACTION = null;
				ENUM_SB_HEALTH_DISPLAY_INTEGER = null;
				ENUM_SB_ACTION_CHANGE = null;
				ENUM_SB_ACTION_REMOVE = null;
			}
		} catch (Throwable throwable) { throw new ExceptionInInitializerError(throwable); }
	}
	
	private final Player player;
	private final String id;
	
	private final List<String> lines = new ArrayList<>();
	private String title = ChatColor.RESET.toString();
	
	private boolean deleted = false;
	
	/**
	 * Creates a new SimpleBoard.
	 *
	 * @param player the owner of the scoreboard
	 */
	public SimpleBoard(Player player) {
		this.player = Objects.requireNonNull(player, "player");
		this.id = "xb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());
		
		try {
			sendObjectivePacket(SimpleBoard.ObjectiveMode.CREATE);
			sendDisplayObjectivePacket();
		} catch (Throwable throwable) { throw new RuntimeException("Unable to create scoreboard", throwable); }
	}
	
	/**
	 * Get the scoreboard title.
	 *
	 * @return the scoreboard title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Update the scoreboard title.
	 *
	 * @param title the new scoreboard title
	 * @throws IllegalArgumentException if the title is longer than 32 chars on 1.12 or lower
	 * @throws IllegalStateException    if {@link #delete()} was call before
	 */
	public void updateTitle(String title) {
		if (this.title.equals(Objects.requireNonNull(title, "title"))) return;
		
		if (!SimpleBoard.VersionType.V1_13.isHigherOrEqual() && title.length() > 32) {
			throw new IllegalArgumentException("Title is longer than 32 chars");
		}
		
		this.title = title;
		
		try { sendObjectivePacket(SimpleBoard.ObjectiveMode.UPDATE); }
		catch (Throwable throwable) { throw new RuntimeException("Unable to update scoreboard title", throwable); }
	}
	
	/**
	 * Get the scoreboard lines.
	 *
	 * @return the scoreboard lines
	 */
	public List<String> getLines() {
		return new ArrayList<>(this.lines);
	}
	
	/**
	 * Get the specified scoreboard line.
	 *
	 * @param line the line number
	 * @return the line
	 * @throws IndexOutOfBoundsException if the line is higher than {@code size}
	 */
	public String getLine(int line) {
		checkLineNumber(line, true, false);
		
		return this.lines.get(line);
	}
	
	/**
	 * Update a single scoreboard line.
	 *
	 * @param line the line number
	 * @param text the new line text
	 * @throws IndexOutOfBoundsException if the line is higher than the size.
	 */
	public synchronized void updateLine(int line, String text) {
		checkLineNumber(line, false, true);
		
		try {
			if (line < this.lines.size()) {
				this.lines.set(line, text);
				
				sendTeamPacket(getScoreByLine(line), SimpleBoard.TeamMode.UPDATE);
				return;
			}
			
			List<String> newLines = new ArrayList<>(this.lines);
			
			if (line > this.lines.size()) {
				for (int i = this.lines.size(); i < line; i++) {
					newLines.add("");
				}
			}
			
			newLines.add(text);
			
			updateLines(newLines);
		} catch (Throwable throwable) { throw new RuntimeException("Unable to update scoreboard lines", throwable); }
	}
	
	/**
	 * Remove a scoreboard line.
	 *
	 * @param line the line number
	 */
	public synchronized void removeLine(int line) {
		checkLineNumber(line, false, false);
		
		if (line >= this.lines.size()) return;
		
		List<String> newLines = new ArrayList<>(this.lines);
		newLines.remove(line);
		updateLines(newLines);
	}
	
	/**
	 * Update all the scoreboard lines.
	 *
	 * @param lines the new lines
	 * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
	 * @throws IllegalStateException    if {@link #delete()} was call before
	 */
	public void updateLines(String... lines) {
		updateLines(Arrays.asList(lines));
	}
	
	/**
	 * Update the lines of the scoreboard
	 *
	 * @param lines the new scoreboard lines
	 * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
	 * @throws IllegalStateException    if {@link #delete()} was call before
	 */
	public synchronized void updateLines(Collection<String> lines) {
		Objects.requireNonNull(lines, "lines");
		checkLineNumber(lines.size(), false, true);
		
		if (!SimpleBoard.VersionType.V1_13.isHigherOrEqual()) {
			int lineCount = 0;
			for (String s : lines) {
				if (s != null && s.length() > 30) throw new IllegalArgumentException("Line " + lineCount + " is longer than 30 chars");
				
				lineCount++;
			}
		}
		
		List<String> oldLines = new ArrayList<>(this.lines);
		this.lines.clear();
		this.lines.addAll(lines);
		
		int linesSize = this.lines.size();
		int oldLinesSize = oldLines.size();
		
		try {
			if (oldLinesSize != linesSize) {
				List<String> oldLinesCopy = new ArrayList<>(oldLines);
				
				if (oldLinesSize > linesSize) {
					for (int i = oldLinesCopy.size(); i > linesSize; i--) {
						sendTeamPacket(i - 1, SimpleBoard.TeamMode.REMOVE);
						sendScorePacket(i - 1, SimpleBoard.ScoreboardAction.REMOVE);
						
						oldLines.remove(0);
					}
				} else {
					for (int i = oldLinesCopy.size(); i < linesSize; i++) {
						sendScorePacket(i, SimpleBoard.ScoreboardAction.CHANGE);
						sendTeamPacket(i, SimpleBoard.TeamMode.CREATE);
						
						oldLines.add(oldLinesSize - i, getLineByScore(i));
					}
				}
			}
			
			for (int i = 0; i < linesSize; i++) {
				if (!Objects.equals(getLineByScore(oldLines, i), getLineByScore(i))) {
					sendTeamPacket(i, SimpleBoard.TeamMode.UPDATE);
				}
			}
		} catch (Throwable throwable) { throw new RuntimeException("Unable to update scoreboard lines", throwable); }
	}
	
	/**
	 * Get the player who has the scoreboard.
	 *
	 * @return current player for this FastBoard
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Get the scoreboard id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Get if the scoreboard is deleted.
	 *
	 * @return true if the scoreboard is deleted
	 */
	public boolean isDeleted() {
		return this.deleted;
	}
	
	/**
	 * Get the scoreboard size (the number of lines).
	 *
	 * @return the size
	 */
	public int size() {
		return this.lines.size();
	}
	
	/**
	 * Delete this FastBoard, and will remove the scoreboard for the associated player if he is online.
	 * After this, all uses of {@link #updateLines} and {@link #updateTitle} will throws an {@link IllegalStateException}
	 *
	 * @throws IllegalStateException if this was already call before
	 */
	public void delete() {
		try {
			int size = this.lines.size();
			for (int i = 0; i < size; i++) sendTeamPacket(i, SimpleBoard.TeamMode.REMOVE);
			
			sendObjectivePacket(SimpleBoard.ObjectiveMode.REMOVE);
		} catch (Throwable throwable) { throw new RuntimeException("Unable to delete scoreboard", throwable); }
		
		this.deleted = true;
	}
	
	/**
	 * Return if the player has a prefix/suffix characters limit.
	 * By default, it returns true only in 1.12 or lower.
	 * This method can be overridden to fix compatibility with some versions support plugin.
	 *
	 * @return max length
	 */
	protected boolean hasLinesMaxLength() {
		return !SimpleBoard.VersionType.V1_13.isHigherOrEqual();
	}
	
	private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
		if (line < 0) throw new IllegalArgumentException("Line number must be positive");
		
		if (checkInRange && line >= this.lines.size()) throw new IllegalArgumentException("Line number must be under " + this.lines.size());
		
		if (checkMax && line >= COLOR_CODES.length - 1) throw new IllegalArgumentException("Line number is too high: " + line);
	}
	
	private int getScoreByLine(int line) {
		return this.lines.size() - line - 1;
	}
	
	private String getLineByScore(int score) {
		return getLineByScore(this.lines, score);
	}
	
	private String getLineByScore(List<String> lines, int score) {
		return lines.get(lines.size() - score - 1);
	}
	
	private void sendObjectivePacket(SimpleBoard.ObjectiveMode mode) throws Throwable {
		Object packet = PACKET_SB_OBJ.invoke();
		
		setField(packet, String.class, this.id);
		setField(packet, int.class, mode.ordinal());
		
		if (mode != SimpleBoard.ObjectiveMode.REMOVE) {
			setComponentField(packet, this.title, 1);
			
			if (SimpleBoard.VersionType.V1_8.isHigherOrEqual()) setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
		} else if (VERSION_TYPE == SimpleBoard.VersionType.V1_7) setField(packet, String.class, "", 1);
		
		sendPacket(packet);
	}
	
	private void sendDisplayObjectivePacket() throws Throwable {
		Object packet = PACKET_SB_DISPLAY_OBJ.invoke();
		
		setField(packet, int.class, 1); // Position (1: sidebar)
		setField(packet, String.class, this.id); // Score Name
		
		sendPacket(packet);
	}
	
	private void sendScorePacket(int score, SimpleBoard.ScoreboardAction action) throws Throwable {
		Object packet = PACKET_SB_SCORE.invoke();
		
		setField(packet, String.class, COLOR_CODES[score], 0); // Player Name
		
		if (SimpleBoard.VersionType.V1_8.isHigherOrEqual()) {
			setField(packet, ENUM_SB_ACTION, action == SimpleBoard.ScoreboardAction.REMOVE
				 ? ENUM_SB_ACTION_REMOVE
				 : ENUM_SB_ACTION_CHANGE);
		} else setField(packet, int.class, action.ordinal(), 1); // Action
		
		if (action == SimpleBoard.ScoreboardAction.CHANGE) {
			setField(packet, String.class, this.id, 1); // Objective Name
			setField(packet, int.class, score); // Score
		}
		
		sendPacket(packet);
	}
	
	private void sendTeamPacket(int score, SimpleBoard.TeamMode mode) throws Throwable {
		if (mode == SimpleBoard.TeamMode.ADD_PLAYERS || mode == SimpleBoard.TeamMode.REMOVE_PLAYERS) throw new UnsupportedOperationException();
		
		int maxLength = hasLinesMaxLength() ? 16 : 1024;
		Object packet = PACKET_SB_TEAM.invoke();
		
		setField(packet, String.class, this.id + ':' + score); // Team name
		setField(packet, int.class, mode.ordinal(), VERSION_TYPE == SimpleBoard.VersionType.V1_8 ? 1 : 0); // Update mode
		
		if (mode == SimpleBoard.TeamMode.CREATE || mode == SimpleBoard.TeamMode.UPDATE) {
			String line = getLineByScore(score);
			String prefix;
			String suffix = null;
			
			if (line == null || line.isEmpty()) prefix = COLOR_CODES[score] + ChatColor.RESET;
			else if (line.length() <= maxLength) prefix = line;
			else {
				// Prevent splitting color codes
				int index = line.charAt(maxLength - 1) == ChatColor.COLOR_CHAR ? (maxLength - 1) : maxLength;
				prefix = line.substring(0, index);
				String suffixTmp = line.substring(index);
				ChatColor chatColor = null;
				
				if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR) chatColor = ChatColor.getByChar(suffixTmp.charAt(1));
				
				String color = ChatColor.getLastColors(prefix);
				boolean addColor = chatColor == null || chatColor.isFormat();
				
				suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
			}
			
			if (prefix.length() > maxLength || (suffix != null && suffix.length() > maxLength)) {
				// Something went wrong, just cut to prevent client crash/kick
				prefix = prefix.substring(0, maxLength);
				suffix = (suffix != null) ? suffix.substring(0, maxLength) : null;
			}
			
			if (SimpleBoard.VersionType.V1_17.isHigherOrEqual()) {
				Object team = PACKET_SB_SERIALIZABLE_TEAM.invoke();
				
				// Since the packet is initialized with null values, we need to change more things.
				setComponentField(team, "", 0); // Display name
				setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING); // Color
				setComponentField(team, prefix, 1); // Prefix
				setComponentField(team, suffix == null ? "" : suffix, 2); // Suffix
				setField(team, String.class, "always", 0); // Visibility
				setField(team, String.class, "always", 1); // Collisions
				setField(packet, Optional.class, Optional.of(team));
			} else {
				setComponentField(packet, prefix, 2); // Prefix
				setComponentField(packet, suffix == null ? "" : suffix, 3); // Suffix
				setField(packet, String.class, "always", 4); // Visibility for 1.8+
				setField(packet, String.class, "always", 5); // Collisions for 1.9+
			}
			
			if (mode == SimpleBoard.TeamMode.CREATE) {
				setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score])); // Players in the team
			}
		}
		
		sendPacket(packet);
	}
	
	private void sendPacket(Object packet) throws Throwable {
		if (this.deleted) throw new IllegalStateException("This FastBoard is deleted");
		
		if (this.player.isOnline()) SEND_PACKET.invoke(PLAYER_CONNECTION.invoke(PLAYER_GET_HANDLE.invoke(this.player)), packet);
	}
	
	private void setField(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
		setField(object, fieldType, value, 0);
	}
	
	private void setField(Object packet, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
		int i = 0;
		for (Field field : PACKETS.get(packet.getClass())) {
			if (field.getType() == fieldType && count == i++) field.set(packet, value);
		}
	}
	
	private void setComponentField(Object packet, String value, int count) throws Throwable {
		if (!SimpleBoard.VersionType.V1_13.isHigherOrEqual()) {
			setField(packet, String.class, value, count);
			return;
		}
		
		int i = 0;
		for (Field field : PACKETS.get(packet.getClass())) {
			if ((field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLASS) && count == i++) {
				field.set(packet, value.isEmpty()
					 ? EMPTY_MESSAGE
					 : Array.get(MESSAGE_FROM_STRING.invoke(value), 0));
			}
		}
	}
	
	enum ObjectiveMode {
		CREATE, REMOVE, UPDATE
	}
	
	enum TeamMode {
		CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
	}
	
	enum ScoreboardAction {
		CHANGE, REMOVE
	}
	
	enum VersionType {
		V1_7, V1_8, V1_13, V1_17;
		
		public boolean isHigherOrEqual() {
			return VERSION_TYPE.ordinal() >= ordinal();
		}
	}
}
