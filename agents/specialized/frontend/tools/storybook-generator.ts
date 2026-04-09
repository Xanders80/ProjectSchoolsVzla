// Storybook Generator - Frontend Tool for SMS
// Generates storybook-like documentation for Thymeleaf components

export interface StoryConfig {
  component: string;
  module: string;
  variants: StoryVariant[];
}

export interface StoryVariant {
  name: string;
  data: Record<string, unknown>;
}

export class StorybookGenerator {
  generate(config: StoryConfig): string {
    let story = `# ${config.component} Stories\n\n`;
    story += `## Module: ${config.module}\n\n`;

    for (const variant of config.variants) {
      story += `### ${variant.name}\n`;
      story += '```html\n';
      story += `<!-- Data: ${JSON.stringify(variant.data)} -->\n`;
      story += `<!-- Template: ${config.module}/${config.component}.html -->\n`;
      story += '```\n\n';
    }

    return story;
  }

  generateAllComponents(modules: string[]): string {
    let allStories = '# Thymeleaf Component Stories - SMS\n\n';
    for (const module of modules) {
      allStories += `## ${module}\n\n`;
      allStories += `### List Template\n- Default state\n- Empty state\n- Error state\n\n`;
      allStories += `### Form Template\n- New form\n- Edit form\n- Validation errors\n\n`;
      allStories += `### View Template\n- Detail view\n- Related data\n\n`;
    }
    return allStories;
  }
}
