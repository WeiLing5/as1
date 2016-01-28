package ningwei.ningwei_fueltrack;

/**
 * Created by jackxcn on 2016-01-15.
 */
public class FuelTrack {

    private String _date, _station, _odometer, _fuelgrade, _fuelamount, _fuelunitcost;//, _fuelcost;
    //private int fuelcosts, fuelamounts, fuelunitcosts;
    public FuelTrack(String date, String station, String odometer, String fuelgrade,
                     String fuelamount, String fuelunitcost){//, String fuelcost){
        _date = date;
        _station = station;
        _odometer = odometer;
        _fuelgrade = fuelgrade;
        _fuelamount = fuelamount;
        _fuelunitcost = fuelunitcost;
        //_fuelcost = fuelcost;
    }

    public String get_date() {
        return _date;
    }

    public String get_station() {
        return _station;
    }

    public String get_odometer() {
        return _odometer;
    }

    public String get_fuelgrade() {
        return _fuelgrade;
    }

    public String get_fuelamount() {
        return _fuelamount;
    }

    public String get_fuelunitcost() {
        return _fuelunitcost;
    }
}
