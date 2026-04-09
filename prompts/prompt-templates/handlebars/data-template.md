<!-- Handlebars Data Template -->
<script id="data-template" type="text/x-handlebars-template">
  <table class="table">
    <thead>
      <tr>
        {{#each headers}}<th>{{this}}</th>{{/each}}
      </tr>
    </thead>
    <tbody>
      {{#each rows}}
      <tr>
        {{#each this}}<td>{{this}}</td>{{/each}}
      </tr>
      {{/each}}
    </tbody>
  </table>
</script>
