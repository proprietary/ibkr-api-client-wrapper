# RxIBKR

**Disclaimer: Do not use this in production for real trading. It is neither complete nor fully tested.**

This is a wrapper for the Interactive Brokers® TWS API redesigned to use the Reactor Pattern. I was using the TWS API for a systematic trading project and ended up implementing something very messy. This library attempts to define a clean API for everything the TWS API does, using modern Java and reactive streams instead of callbacks.

IBKR's code is included in-tree under `src/main/java/com/ib/...` and has its own license. This is for convenience during development, but it may in the future be replaced by the JAR distributed by IBKR to clearly deliniate which code is owned by me versus by IBKR.

## License

IBKR's code is licensed under the ["TWS API Non-Commercial License"](src/main/java/com/ib/TWS_API_Non-Commercial_License.txt).

All other original code is licensed under the Apache-2.0 software license.

## Legal Disclaimer

This open source software library (the "Software") is provided **"as is"**, without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event shall the authors, copyright holders, or contributors be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the Software or the use or other dealings in the Software.

The Software is intended for use in financial services; however, users should note that there are **no guarantees** of performance or accuracy. The Software should not be relied upon as the sole basis for any financial decisions or transactions. Users are advised to consult with a qualified professional before making any financial decisions.

By using the Software, you acknowledge that you have read this disclaimer and understand that **no warranties** are provided with the Software, and that the authors, copyright holders, and contributors shall not be held responsible for any damages resulting from its use.