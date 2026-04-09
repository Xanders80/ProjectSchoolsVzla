<!-- Handlebars Navigation Buttons Template -->
<div class="nav-buttons">
  {{#each buttons}}
  <button class="btn btn-{{this.style}}" onclick="location.href='{{this.href}}'">{{this.label}}</button>
  {{/each}}
</div>
