package fedffm.ribbit;

import android.graphics.Bitmap;


public class Character {
    private static final String  LOG_TAG         = "Character";
    private static final boolean LOGGING_ENABLED = true;
    private static final boolean DEBUG           = true;

    private char    name;
    private int     ascii;
    private int     ratioClass;
    private int     featureClass;
    private Bitmap  bitmap;


    // Default
    public Character() {
        this.name         = '?';
        this.bitmap       = null;
    }

    // Non-default
    public Character(Bitmap bitmap) {
        this.name         = '?';
        this.bitmap       = bitmap;
        PreliminaryClassifier.determineRatioClass(this);
        PreliminaryClassifier.determineFeatureClass(this);
    }

    // Setters
    public  void setName(char name)                {this.name = name;}
    public  void setAscii(Integer ascii)           {this.ascii = ascii;}
    public  void setBitmap(Bitmap bitmap)          {this.bitmap = bitmap;}
    public  void setRatioClass(int ratioClass)     {this.ratioClass = ratioClass;}
    public  void setFeatureClass(int featureClass) {this.featureClass = featureClass;}

    // Getters
    public char    getName()         {return this.name;}
    public Integer getAscii()        {return this.ascii;}
    public Bitmap  getBitmap()       {return this.bitmap;}
    public int     getRatioClass()   {return this.ratioClass;}
    public int     getFeatureClass() {return this.featureClass;}

    /**
     * Calculate a scaled size value based upon the area (in pixels)
     * of the character's bitmap. This will help us estimate whether
     * or not the segment is actually a single character
     * @return Return a scaled value which represents the size of the segment
     */
    public float sizeValue() {
        assert bitmap != null;

        return ((float)bitmap.getWidth() * (float)(bitmap.getHeight()) / 1500);
    }
}