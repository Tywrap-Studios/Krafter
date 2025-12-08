import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  srcDir: "pages",
  base: "/Krafter/",

  title: "Krafter",
  description: "Content-rich bot software",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      {text: 'Home', link: '/'},
      {text: 'Usage', link: '/users/getting-started'},
      {text: 'API', link: '/api/getting-started'},
      {text: 'Config', link: '/config'}
    ],

    sidebar: {
      'users/': [{
        text: 'Usage',
        items: [
          {text: 'Getting Started', link: '/users/getting-started'},
          {text: 'Modules', items: [
              {text: 'AMA', link: '/users/modules/ext-ama'},
              {text: 'Embed Channels', link: '/users/modules/ext-embed-channels'},
              {text: 'Fun and Utility', items: [
                  {text: 'Funtility', link: 'users/modules/funtility/funtility'},
                  {text: 'Utility', link: 'users/modules/funtility/utility'},
                ]
              },
              {text: 'Log Parser', link: '/users/modules/ext-log-parser'},
              {text: 'Minecraft', link: '/users/modules/ext-minecraft'},
              {text: 'Safety & Abuse', link: '/users/modules/ext-safety-and-abuse'},
              {text: 'Suggestions', link: '/users/modules/ext-suggestions'},
              {text: 'Tags', link: '/users/modules/ext-tags'},
            ],
            collapsed: false
          },
          {text: 'PluralKit', link: '/users/pluralkit'},
          {text: 'Finding the files', link: 'config/where-are-they'},
        ]
      }],
      'config/': [{
        text: 'Configuration',
        items: [
          {text: 'Introduction', link: '/config/'},
          {text: 'Where are the config files?', link: '/config/where-are-they'},
        ]
      }]
    },

    socialLinks: [
      {icon: 'github', link: 'https://github.com/Tywrap-Studios/Krafter'},
      {icon: 'youtube', link: 'https://youtube.com/'}
    ],
  },

  cleanUrls: true
})
