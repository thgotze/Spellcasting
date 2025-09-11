package com.gotze.spellcasting.util;

import net.kyori.adventure.text.Component;

public class StringUtils {

    public static Component convertToSmallFont(Component input) {
        return Component.text(convertToSmallFont(input.toString()));
    }

    /**
     * Converts a string to a small string using the Minecraft small font
     * <p>"Spellcasting is fun!" -> "ѕᴘᴇʟʟᴄᴀѕᴛɪɴɢ ɪѕ ꜰᴜɴ!"
     */
    public static String convertToSmallFont(String input) {
        StringBuilder smallString = new StringBuilder();
        for (char c : input.toLowerCase().toCharArray()) {
            switch (c) {
                // Numbers
                case '0': smallString.append('₀'); break;
                case '1': smallString.append('₁'); break;
                case '2': smallString.append('₂'); break;
                case '3': smallString.append('₃'); break;
                case '4': smallString.append('₄'); break;
                case '5': smallString.append('₅'); break;
                case '6': smallString.append('₆'); break;
                case '7': smallString.append('₇'); break;
                case '8': smallString.append('₈'); break;
                case '9': smallString.append('₉'); break;
                // Alphabet
                case 'a': smallString.append('ᴀ'); break;
                case 'b': smallString.append('ʙ'); break;
                case 'c': smallString.append('ᴄ'); break;
                case 'd': smallString.append('ᴅ'); break;
                case 'e': smallString.append('ᴇ'); break;
                case 'f': smallString.append('ꜰ'); break;
                case 'g': smallString.append('ɢ'); break;
                case 'h': smallString.append('ʜ'); break;
                case 'i': smallString.append('ɪ'); break;
                case 'j': smallString.append('ᴊ'); break;
                case 'k': smallString.append('ᴋ'); break;
                case 'l': smallString.append('ʟ'); break;
                case 'm': smallString.append('ᴍ'); break;
                case 'n': smallString.append('ɴ'); break;
                case 'o': smallString.append('ᴏ'); break;
                case 'p': smallString.append('ᴘ'); break;
                case 'q': smallString.append('ǫ'); break;
                case 'r': smallString.append('ʀ'); break;
                case 's': smallString.append('ѕ'); break;
                case 't': smallString.append('ᴛ'); break;
                case 'u': smallString.append('ᴜ'); break;
                case 'v': smallString.append('ᴠ'); break;
                case 'w': smallString.append('ᴡ'); break;
                case 'x': smallString.append('x'); break;
                case 'y': smallString.append('ʏ'); break;
                case 'z': smallString.append('ᴢ'); break;
                default: smallString.append(c); break;
            }
        }
        return smallString.toString();
    }

    public static String toRomanNumeral(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(level);
        };
    }
}