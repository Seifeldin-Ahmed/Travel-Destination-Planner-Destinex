import { Destination, PageResponse } from '../models/destination.models';

function unwrapRecord(raw: Record<string, unknown>): Record<string, unknown> {
  const nested = raw['data'] ?? raw['body'] ?? raw['result'] ?? raw['payload'];
  if (nested && typeof nested === 'object' && !Array.isArray(nested)) {
    return nested as Record<string, unknown>;
  }
  return raw;
}

/** Maps common Spring / JSON shapes to our Destination model so the UI always renders. */
export function normalizeDestination(raw: unknown): Destination {
  if (!raw || typeof raw !== 'object') {
    return { id: 0, country: '', capital: '', region: '', population: 0, currency: '' };
  }
  const o = unwrapRecord(raw as Record<string, unknown>);
  const id = Number(o['id'] ?? o['destinationId'] ?? 0);
  const country = String(
    o['country'] ?? o['name'] ?? o['countryName'] ?? o['destinationName'] ?? ''
  );
  const imageUrl = o['imageUrl'] ?? o['flag'] ?? o['image'] ?? o['imageURL'];
  return {
    id: Number.isFinite(id) ? id : 0,
    country,
    capital: String(o['capital'] ?? ''),
    region: String(o['region'] ?? ''),
    population: Number(o['population'] ?? 0) || 0,
    currency: String(o['currency'] ?? ''),
    ...(imageUrl != null && String(imageUrl).trim()
      ? { imageUrl: String(imageUrl) }
      : {}),
  };
}

export function normalizePageResponse(raw: unknown): PageResponse {
  if (!raw || typeof raw !== 'object') {
    return { content: [], totalElements: 0, elementsPerPage: 0 };
  }
  const o = raw as Record<string, unknown>;
  const contentRaw = o['content'];
  const content = Array.isArray(contentRaw)
    ? contentRaw.map((x) => normalizeDestination(x))
    : [];
  return {
    content,
    totalElements: Number(o['totalElements'] ?? content.length) || 0,
    elementsPerPage: Number(o['elementsPerPage'] ?? content.length) || 0,
  };
}

export function normalizeDestinationList(raw: unknown): Destination[] {
  if (Array.isArray(raw)) {
    return raw.map((x) => normalizeDestination(x));
  }
  if (raw && typeof raw === 'object' && 'content' in raw) {
    return normalizePageResponse(raw).content;
  }
  if (raw && typeof raw === 'object' && !Array.isArray(raw)) {
    return [normalizeDestination(raw)];
  }
  return [];
}
