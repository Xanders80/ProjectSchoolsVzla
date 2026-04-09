<!-- Extended Navigation Template 2 -->
<nav class="extended-2">
  <ul>
    {{#each items}}
    <li><a href="{{this.href}}">{{this.label}}</a></li>
    {{/each}}
  </ul>
  <div class="right-actions">{{{actions}}}</div>
</nav>
