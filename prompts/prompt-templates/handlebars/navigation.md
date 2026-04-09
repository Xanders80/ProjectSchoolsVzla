<!-- Handlebars Navigation Template -->
<nav>
  <ul>
    {{#each links}}
      <li><a href="{{this.href}}">{{this.label}}</a></li>
    {{/each}}
  </ul>
</nav>
