package fedffm.bytewrite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.List;

public class Identifier {
    private final static String LOG_TAG = "Identifier";
    private final static int    A_ASCII = 97;
    private final static int    Z_ASCII = 122;

    /**
     * In order to more accurately compare the unknown character against any given
     * sample character, scale the unknown character to be the exact same size as the sample
     * character
     * @param sampleCharacter The bitmap for a known character from our CharacterBase
     * @param unknownCharacter The bitmap for the character we are trying to identify
     * @return A bitmap of the unknown character with the same dimensions of the sample character
     */
    private static Bitmap matchSize(Bitmap sampleCharacter, Bitmap unknownCharacter) {
        // Get the dimensions of the sample bitmap
        int w = sampleCharacter.getWidth();
        int h = sampleCharacter.getHeight();

        // Create a bitmap of the unknown character using the dimensions of the sample
        return Bitmap.createScaledBitmap(unknownCharacter, w, h, false);
    }

    /**
     * Walk through each of the bitmaps and see which pixels match, versus which do not.
     * @param sampleCharacter The bitmap for a known character from our CharacterBase
     * @param unknownCharacterScaled The bitmap for the character we are trying to identify. This
     *                               must be scaled to the exact same size as the bitmap of the
     *                               sample character
     * @return A percentage representing how similar the bitmaps are.
     */
    private static float similarity(Bitmap sampleCharacter, Bitmap unknownCharacterScaled) {
        // Ensure that the two bitmaps are the exact same size. This is critical
        if (sampleCharacter.getWidth() != unknownCharacterScaled.getWidth() ||
            sampleCharacter.getHeight() != unknownCharacterScaled.getHeight())
                return (float)0.0;

        // Store the width and height in a variable for easy reference
        int width  = sampleCharacter.getWidth();
        int height = sampleCharacter.getHeight();

        // Store how many pixels there are and how many matching pixels there are
        int blackPixels = 0;
        int matchingPixels = 0;

        // Iterate through the bitmap
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y) {
                // How many black pixels are in the sample bitmap?
                if (sampleCharacter.getPixel(x, y) == Color.BLACK) {
                    blackPixels++;
                    // If the unknown character also has a black pixel at the
                    // same location, we have found a match!
                    if  (unknownCharacterScaled.getPixel(x, y) == Color.BLACK)
                        matchingPixels++;
                }
            }

        //   0.0 == the two characters are completely different
        // 100.0 == the two characters are an identical match
        return ((float)matchingPixels / (float)blackPixels) * (float)100.00;
    }

    /**
     * Identify a single character
     * @param unknown The character to be identified
     * @return The character updated with a name and ASCII code
     */
    public static Character identify(Character unknown, Context context) {
        // Start by getting the entire character base
        List<Character> characterBase;

        // Use these to find the greatest similarity
        float similarity;
        float greatestSimilarity;
        float averageSimilarity;
        float totalSimilarity;
        float greatestTotalSimilarity = (float)0.0;
        float greatestAverageSimilarity = (float)0.0;
        int   matchIndex = 0;

        // Compare our unknown character against every character
        for (int i = A_ASCII; i <= Z_ASCII; ++i) {
            // Get the full list of samples for each different type of character/letter
            characterBase = CharacterBase.getInstance(context).getCharacterSamples((char)i);

            // Keep track of the similarity scores for each character
            greatestSimilarity = (float)0.0;
            averageSimilarity  = (float)0.0;

            // Iterate through each sample in the list for the current character
            for (int j = 0; j < characterBase.size(); ++j) {
                // Compare the bitmap of the unknown character against the current sample
                Bitmap unknownScaled = matchSize(characterBase.get(j).getBitmap(), unknown.getBitmap());
                similarity = similarity(characterBase.get(j).getBitmap(), unknownScaled);

                // Keep track of which character has the greatest similarity
                // TODO: This is for debugging purposes
                if (similarity > greatestSimilarity)
                    greatestSimilarity = similarity;

                // Compute the average similarity for the given character
                averageSimilarity += similarity;
            }

            // Calculate the average similarity for the character we finished iterating through
            averageSimilarity = averageSimilarity / characterBase.size();
            totalSimilarity   = (averageSimilarity + greatestSimilarity) / (float)2.0;

//             Log.i(LOG_TAG, "character:          " + (char)i);
//            Log.i(LOG_TAG, "greatest similarity: " + greatestSimilarity);
//            Log.i(LOG_TAG, "averageSimilarity:   " + averageSimilarity + "\n");
//            Log.i(LOG_TAG, "totalSimilarity: " + totalSimilarity + "\n");

            // Keep track of which character has the highest similarity (total)
            // to the unknown character
            if (totalSimilarity > greatestTotalSimilarity) {
                greatestTotalSimilarity = totalSimilarity;
                matchIndex = i;
            }
        }

        // Log
        Log.i(LOG_TAG, "Greatest total similarity: " + (char)matchIndex +
                " with a total of : " + (greatestTotalSimilarity) + "% similarity\n\n");

        // Set the character name and ascii code
        unknown.setName((char)matchIndex);
        unknown.setAscii(matchIndex);

        return unknown;
    }

    /**
     * Identify a word
     * @param unknownWord The Word to be identified
     * @return A Word consisting of identified characters
     */
    public static Word identify(Word unknownWord, Context context) {
        // Instantiate what will be our word
        Word word = new Word();

        // Examine each character in the word
        for (Character unknownCharacter : unknownWord.getCharacters()) {
            // Identify each character and add it to our word
            word.addCharacter(identify(unknownCharacter,context));
        }
        return word;
    }
}