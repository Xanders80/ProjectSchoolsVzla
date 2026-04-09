<!-- Handlebars Table Template -->
<table class="table">
  <thead>
    <tr>
      {{#each headers}}
      <th>{{this}}</th>
      {{/each}}
    </tr>
  </thead>
  <tbody>
    {{#each rows}}
    <tr>
      {{#each this}}
      <td>{{this}}</td>
      {{/each}}
    </tr>
    {{/each}}
  </tbody>
</table>
