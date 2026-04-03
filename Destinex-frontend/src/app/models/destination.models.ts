export interface DestinationRequest {
  name: string;
  capital: string;
  region: string;
  population: number;
  currency: string;
  flag?: string;
}

export interface Destination {
  id: number;
  country: string;
  capital: string;
  region: string;
  population: number;
  currency: string;
  imageUrl?: string;
}

export interface PageResponse {
  content: Destination[];
  totalElements: number;
  elementsPerPage: number;
}

export interface CountryResponse {
  name: string;
  capital: string;
  region: string;
  population: number;
  currency: string;
  flag: string;
}

export interface ApiResponse {
  status?: string;
  message?: string;
}

export interface SignupRequest {
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  role: 'CUSTOMER' | 'SELLER';
  address?: string;
  phoneNumber?: string;
}
