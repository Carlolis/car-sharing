module default {
  type Trip {
    required name: str;
    required date: cal::local_date;
    required distance: int16;
    required multi drivers: Person;

  }

  type Person {
    required name: str;
    multi drive_in := .<drivers[is Trip];
  }

}
