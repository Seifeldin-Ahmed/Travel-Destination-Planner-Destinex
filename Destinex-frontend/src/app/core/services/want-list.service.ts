import { Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { DestinationService } from './destination.service';

/** Legacy client-only wishlist keys — remove if present (wishlist is server-backed now). */
function purgeLegacyWishlistStorage(): void {
  try {
    if (typeof localStorage === 'undefined') return;
    const keys: string[] = [];
    for (let i = 0; i < localStorage.length; i++) {
      const k = localStorage.key(i);
      if (k?.startsWith('travel_want_')) keys.push(k);
    }
    for (const k of keys) localStorage.removeItem(k);
  } catch {
    /* ignore */
  }
}

@Injectable({ providedIn: 'root' })
export class WantListService {
  private readonly ids = signal<Set<number>>(new Set());

  constructor(
    private readonly auth: AuthService,
    private readonly api: DestinationService
  ) {
    purgeLegacyWishlistStorage();
  }

  syncUser(): void {
    const u = this.auth.user();
    if (!u) {
      this.ids.set(new Set());
      return;
    }
    this.api.getWishlist().subscribe({
      next: (list) => {
        const next = new Set<number>();
        for (const d of list) {
          if (d.id) next.add(d.id);
        }
        this.ids.set(next);
      },
      error: () => {
        this.ids.set(new Set());
      },
    });
  }

  wants(id: number): boolean {
    return this.ids().has(id);
  }

  async toggle(id: number): Promise<void> {
    const u = this.auth.user();
    if (!u) return;
    const has = this.ids().has(id);
    if (has) {
      await firstValueFrom(this.api.removeFromWishlist(id));
      const next = new Set(this.ids());
      next.delete(id);
      this.ids.set(next);
    } else {
      await firstValueFrom(this.api.addToWishlist(id));
      const next = new Set(this.ids());
      next.add(id);
      this.ids.set(next);
    }
  }
}
