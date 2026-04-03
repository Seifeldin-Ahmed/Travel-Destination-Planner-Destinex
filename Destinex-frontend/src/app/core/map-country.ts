import { CountryResponse, DestinationRequest } from '../models/destination.models';

export function countryToDestinationRequest(c: CountryResponse): DestinationRequest {
  return {
    name: c.name,
    capital: c.capital,
    region: c.region,
    population: c.population > 0 ? c.population : 1,
    currency: c.currency,
    ...(c.flag ? { flag: c.flag } : {}),
  };
}
