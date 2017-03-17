package Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Roy on 12-Aug-16.
 */
public class WeatherList {
    private ArrayList<WeatherData> weatherList;

    public WeatherList(){
        weatherList = new ArrayList<WeatherData>();
    }

    private WeatherList(InputStream inputStream){
        InputStreamReader is = null;
        is = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(is);
        weatherList = new ArrayList<WeatherData>();
        try {
            reader.readLine();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    this.addWeatherData(Integer.parseInt(data[0]), "0".concat(data[1]), data[2], Float.parseFloat(data[3]), Float.parseFloat(data[4]), Integer.parseInt(data[5]), data[6], Float.parseFloat(data[7]), Float.parseFloat(data[8]), Float.parseFloat(data[9]), Float.parseFloat(data[10]), Integer.parseInt(data[11]), data[12], Float.parseFloat(data[13]), Float.parseFloat(data[14]), Float.parseFloat(data[15]), Float.parseFloat(data[16]), Integer.parseInt(data[17]), data[18], Float.parseFloat(data[19]), Float.parseFloat(data[20]), Float.parseFloat(data[21]), Float.parseFloat(data[22]), Float.parseFloat(data[23]), Float.parseFloat(data[24]), Float.parseFloat(data[25]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCount(){
        return weatherList.size();
    }

    public void addWeatherData(int id, String postcode, String suburb, float latitude, float longitude, int solarstation, String solarname, float solardistance, float solarwet, float solardry, float solarmean, int rainstation, String rainname, float raindistance, float rainwet, float raindry, float rainmean, int tempstation, String tempname, float tempdistance, float tempmaxwet, float tempmaxdry, float tempmaxmean, float tempminwet, float tempmindry, float tempminmean){
        WeatherData newWeatherData = new WeatherData(id, postcode, suburb, latitude, longitude, solarstation, solarname, solardistance, solarwet, solardry, solarmean, rainstation, rainname, raindistance, rainwet, raindry, rainmean, tempstation, tempname, tempdistance, tempmaxwet, tempmaxdry, tempmaxmean, tempminwet, tempmindry, tempminmean);
        weatherList.add(newWeatherData);
    }

    public WeatherData getWeatherDataByIndex(int index){
        return weatherList.get(index);
    }

    public WeatherData getWeatherDataByID(int id){
        WeatherData weatherData = null;
        for (int index = 0; index < weatherList.size(); index++){
            if(weatherList.get(index).getId() == id){
                weatherData = (WeatherData) weatherList.get(index);
                break;
            }
        }
        return weatherData;
    }

    public WeatherData getWeatherDataByPostCodeAndSuburb(String postcode, String suburb){
        WeatherData weatherData = null;
        for (int index = 0; index < weatherList.size(); index++){
            if(weatherList.get(index).getPostcode().compareToIgnoreCase(postcode) == 0 && weatherList.get(index).getSuburb().compareToIgnoreCase(suburb) == 0){
                weatherData =  weatherList.get(index);
                break;
            }
        }
        return weatherData;
    }

    public WeatherData getWeatherDataByDisplayName(String displayName){
        WeatherData weatherData = null;
        for (int index = 0; index < weatherList.size(); index++){
            if(weatherList.get(index).getPostcode().concat(" ").concat(weatherList.get(index).getSuburb()).compareToIgnoreCase(displayName) == 0){
                weatherData = weatherList.get(index);
                break;
            }
        }
        return weatherData;
    }
}
