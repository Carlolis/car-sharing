module default {
  type TripEdge {
    required name: str;
    required date: cal::local_date;
    required distance: int16;
    required multi drivers: PersonEdge;

  }

  type PersonEdge {
    required name: str;
    multi drive_in := .<drivers[is TripEdge];
  }



}
using extension ai;