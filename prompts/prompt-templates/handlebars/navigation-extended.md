<!-- Extended Navigation Template -->
<nav class="extended">
  <ul>
    {{#each items}}
    <li><a href="{{this.href}}">{{this.label}}</a></li>
    {{/each}}
  </ul>
  <button class="btn btn-secondary" onclick="location.href='{{home.href}}'">Home</button>
</nav>
