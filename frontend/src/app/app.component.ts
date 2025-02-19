import {Component} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {TITLE} from './app.config';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  constructor(private titleService: Title) {
    this.titleService.setTitle(TITLE)
  }
}
