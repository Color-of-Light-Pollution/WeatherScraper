# WeatherScraper

This program is a standalone application that can grab and store in a CSV format current weather data for the city of San Francisco, from Weather.com's webpage.

Input: No input necessary; just run the .JAR file.
Output: Appends to (or creates if nonexistant) WebData.csv, a file in the same directory level as WeatherScraper.JAR 

Data:
Data is stored in a csv, one record per row. The fields in the CSV are as follows:
Date & time, temp(f), weather condition, AQI, Sunrise time, Sunset time, Humidity%, Dew Point(F), Visibility(mi), Wind speed(mph), UV index(/10)
 > Date & time is formatted as follows: 
 > YYYY - MM - DD | HH:00
