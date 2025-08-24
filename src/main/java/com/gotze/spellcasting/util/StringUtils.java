package com.gotze.spellcasting.util;

public class StringUtils {

    // Converts a string to a new string using the Minecraft small font
    // "Spellcasting is fun!" -> "ѕᴘᴇʟʟᴄᴀѕᴛɪɴɢ ɪѕ ꜰᴜɴ!"
    public static String convertToSmallFont(String input) {
        StringBuilder newString = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                // Numbers
                case '0': newString.append('₀'); break;
                case '1': newString.append('₁'); break;
                case '2': newString.append('₂'); break;
                case '3': newString.append('₃'); break;
                case '4': newString.append('₄'); break;
                case '5': newString.append('₅'); break;
                case '6': newString.append('₆'); break;
                case '7': newString.append('₇'); break;
                case '8': newString.append('₈'); break;
                case '9': newString.append('₉'); break;
                // Alphabet
                case 'a': newString.append('ᴀ'); break;
                case 'b': newString.append('ʙ'); break;
                case 'c': newString.append('ᴄ'); break;
                case 'd': newString.append('ᴅ'); break;
                case 'e': newString.append('ᴇ'); break;
                case 'f': newString.append('ꜰ'); break;
                case 'g': newString.append('ɢ'); break;
                case 'h': newString.append('ʜ'); break;
                case 'i': newString.append('ɪ'); break;
                case 'j': newString.append('ᴊ'); break;
                case 'k': newString.append('ᴋ'); break;
                case 'l': newString.append('ʟ'); break;
                case 'm': newString.append('ᴍ'); break;
                case 'n': newString.append('ɴ'); break;
                case 'o': newString.append('ᴏ'); break;
                case 'p': newString.append('ᴘ'); break;
                case 'q': newString.append('ǫ'); break;
                case 'r': newString.append('ʀ'); break;
                case 's': newString.append('ѕ'); break;
                case 't': newString.append('ᴛ'); break;
                case 'u': newString.append('ᴜ'); break;
                case 'v': newString.append('ᴠ'); break;
                case 'w': newString.append('ᴡ'); break;
                case 'x': newString.append('x'); break;
                case 'y': newString.append('ʏ'); break;
                case 'z': newString.append('ᴢ'); break;
                // Default
                default: newString.append(c); break;
            }
        }
        return newString.toString();
    }
}