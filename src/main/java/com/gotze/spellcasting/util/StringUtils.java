package com.gotze.spellcasting.util;

public class StringUtils {

    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;

        String[] words = input.toLowerCase()
                .replace("_", " ")
                .split("\\s+");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1));
            }
            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }


    /**
     * Converts a string to a small string using the Minecraft small font
     * <p>"Spellcasting is fun!" -> "ѕᴘᴇʟʟᴄᴀѕᴛɪɴɢ ɪѕ ꜰᴜɴ!"
     */
    public static String convertToSmallFont(String input) {
        if (input == null || input.isEmpty()) return input;

        StringBuilder smallString = new StringBuilder();
        for (char c : input.toLowerCase().toCharArray()) {
            char smallChar = switch (c) {
                // Numbers
                case '0' -> '₀';
                case '1' -> '₁';
                case '2' -> '₂';
                case '3' -> '₃';
                case '4' -> '₄';
                case '5' -> '₅';
                case '6' -> '₆';
                case '7' -> '₇';
                case '8' -> '₈';
                case '9' -> '₉';
                // Alphabet
                case 'a' -> 'ᴀ';
                case 'b' -> 'ʙ';
                case 'c' -> 'ᴄ';
                case 'd' -> 'ᴅ';
                case 'e' -> 'ᴇ';
                case 'f' -> 'ꜰ';
                case 'g' -> 'ɢ';
                case 'h' -> 'ʜ';
                case 'i' -> 'ɪ';
                case 'j' -> 'ᴊ';
                case 'k' -> 'ᴋ';
                case 'l' -> 'ʟ';
                case 'm' -> 'ᴍ';
                case 'n' -> 'ɴ';
                case 'o' -> 'ᴏ';
                case 'p' -> 'ᴘ';
                case 'q' -> 'ǫ';
                case 'r' -> 'ʀ';
                case 's' -> 'ѕ';
                case 't' -> 'ᴛ';
                case 'u' -> 'ᴜ';
                case 'v' -> 'ᴠ';
                case 'w' -> 'ᴡ';
                case 'x' -> 'x';
                case 'y' -> 'ʏ';
                case 'z' -> 'ᴢ';
                default -> c;
            };
            smallString.append(smallChar);
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