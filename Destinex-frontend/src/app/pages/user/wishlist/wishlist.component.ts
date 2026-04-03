import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { DestinationService } from '../../../core/services/destination.service';
import { WantListService } from '../../../core/services/want-list.service';
import { Destination } from '../../../models/destination.models';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './wishlist.component.html',
  styleUrl: './wishlist.component.css',
})
export class WishlistComponent implements OnInit {
  private readonly api = inject(DestinationService);
  protected readonly wantList = inject(WantListService);

  protected readonly items = signal<Destination[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly removing = signal<number | null>(null);
  protected readonly clearing = signal(false);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getWishlist().subscribe({
      next: (list) => {
        this.items.set(list);
        this.wantList.syncUser();
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load your saved places.');
        this.loading.set(false);
      },
    });
  }

  protected async remove(d: Destination, ev: Event): Promise<void> {
    ev.preventDefault();
    ev.stopPropagation();
    this.removing.set(d.id);
    try {
      await firstValueFrom(this.api.removeFromWishlist(d.id));
      this.items.update((list) => list.filter((x) => x.id !== d.id));
      this.wantList.syncUser();
    } catch {
      this.error.set('Could not remove from wishlist.');
    } finally {
      this.removing.set(null);
    }
  }

  protected async clearAll(): Promise<void> {
    if (!this.items().length) return;
    if (!confirm('Remove all destinations from your saved list?')) return;
    this.clearing.set(true);
    this.error.set(null);
    try {
      await firstValueFrom(this.api.clearWishlist());
      this.items.set([]);
      this.wantList.syncUser();
    } catch {
      this.error.set('Could not clear your wishlist.');
    } finally {
      this.clearing.set(false);
    }
  }
}
