const sonarqubeScanner = require('sonarqube-scanner');

sonarqubeScanner(
  {
    serverUrl: 'https://sonarcloud.io',
    token: process.env.SONAR_TOKEN,
    options: {
      'sonar.projectKey': 'GHT9999_Gestion_Tache',
      'sonar.organization': 'ght9999',
      'sonar.sources': './src',
      'sonar.exclusions': '**/node_modules/**, **/*.spec.ts',
      'sonar.tests': './src',
      'sonar.test.inclusions': '**/*.spec.ts',
      'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
    },
  },
  () => {
    console.log('SonarQube scan completed.');
  }
);
