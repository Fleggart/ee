package squeek.spiceoflife.helpers;

public class StringHelper
{
    public static String decapitalize(String string, java.util.Locale locale)
    {
        if (string == null || string.isEmpty())
            return string;
        else
            return string.substring(0, 1).toLowerCase(locale) + string.substring(1);
    }
}
