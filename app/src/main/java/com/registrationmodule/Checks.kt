
/**
 * Bunch of check methods
 *
 *
 * For internal usage only!
 */
class Checks private constructor() {

    init {
        throw IllegalStateException("No instances please.")
    }

    companion object {

        /**
         * Checks that passed reference is not null,
         * throws [NullPointerException] with passed message if reference is null
         *
         * @param object  to check
         * @param message exception message if object is null
         */
        fun checkNotNull(`object`: Any?, message: String) {
            if (`object` == null) {
                throw NullPointerException(message)
            }
        }

        /**
         * Checks that passed string is not null and not empty,
         * throws [NullPointerException] or [IllegalStateException] with passed message
         * if string is null or empty.
         *
         * @param value   a string to check
         * @param message exception message if object is null
         */
        fun checkNotEmpty(value: String?, message: String) {
            if (value == null) {
                throw NullPointerException(message)
            } else if (value.length == 0) {
                throw IllegalStateException(message)
            }
        }
    }
}
