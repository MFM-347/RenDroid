# AppLate
Android **App Template** with GitHub Workflow Build

This template automates building Android APK and AAB files using GitHub Actions, supporting debug builds and uploading build artifacts for distribution. The workflow is manually triggered via `workflow_dispatch`, with options to specify build names and target testers.

## Usage

### Using the Template
1. **Create a New Repository from Template**:
   - Click on **"Use this template"** at the top of the repository to create your own copy of this Android CI setup.
   - Customize your new repository as needed.

2. **Add Environment Variables**:
   - Go to your repository's **Settings** > **Secrets and variables** > **Actions** > **New repository secret**.
   - Add the following secrets:

### Required Secrets and Environment Variables
1. **Keystore Information** (used to sign the APK and AAB):
   - `KEYSTORE_BASE_64`: Base64-encoded version of your keystore file. To generate this, run:
     ```bash
     base64 <path_to_keystore_file> > keystore.b64
     ```
     Copy the contents of `keystore.b64` and add it as a secret.
   - `DEBUG_KEYSTORE_PASSWORD`: The password for the keystore.
   - `DEBUG_KEYSTORE_ALIAS`: The alias used in the keystore.
   - `DEBUG_KEY_PASSWORD`: The password for the key.

2. **Other Environment Setup**:
   - Ensure the repository includes the `gradlew` file in the root directory and follows the standard Android app directory structure (`app/build/outputs` for APK and AAB outputs).

### Running the Workflow
1. Navigate to the **Actions** tab in your repository.
2. Select the **Android CI** workflow and click **Run workflow**.
3. In the workflow dispatch panel:
   - Enter a custom name for the build (optional).
   - Select a tester group from the available options (`android`, `internalTesters`, `everyone`).

### Accessing Artifacts
After the workflow completes, you can download the generated build artifacts:
1. Go to the workflow run page under the **Actions** tab.
2. Find `release-artifacts` under the uploaded artifacts section.
3. Download `debug-build.zip`, which contains both the APK and AAB files for testing or distribution.

## Credits
This workflow was inspired by **Lloyd Dcosta**'s guide on [Medium](https://medium.com/@dcostalloyd90/automating-android-builds-with-github-actions-a-step-by-step-guide-2a02a54f59cd) and enhanced by [@MFM-347](https://github.com/MFM-347).

## License
The code in this repository is licensed under the **Apache 2.0 License**.

[![License](https://img.shields.io/badge/License-Apache_2.0-0298c3.svg)](https://opensource.org/licenses/Apache-2.0)