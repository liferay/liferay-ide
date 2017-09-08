package com.liferay.ide.idea.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class SwitchConsumer<T> implements Consumer<T> {

	public SwitchConsumer(Map<Predicate<T>, Consumer<T>> cases, Consumer<T> defaultConsumer) {
		_cases = cases;
		_defaultConsumer = defaultConsumer;
	}

	@Override
	public void accept(T t) {
		Optional<Entry<Predicate<T>, Consumer<T>>> matchingCase = 
			_cases.entrySet().stream().filter(
				e -> e.getKey().test(t)
			).findFirst();
		
		if (matchingCase.isPresent()) {
			matchingCase.get().getValue().accept(t);
		}
		else if (_defaultConsumer != null) {
			_defaultConsumer.accept(t);
		}
	}
	
	public static <T> SwitchConsumerBuilder<T> newBuilder() {
		return new SwitchConsumerBuilder<T>();
	}
	
	public static <T> SwitchConsumerBuilder<T> newBuilder(Class<T> clazz) {
		return new SwitchConsumerBuilder<T>();
	}

	public static class SwitchConsumerBuilder<T> {
		private Map<Predicate<T>, Consumer<T>> _cases = new LinkedHashMap<>();

		private Consumer<T> _default;

		public SwitchConsumerBuilder<T> addCase(Predicate<T> p, Consumer<T> c) {
			_cases.put(p, c);

			return this;
		}

		public SwitchConsumerBuilder<T> setDefault(Consumer<T> c) {
			_default = c;

			return this;
		}

		public SwitchConsumer<T> build() {
			return new SwitchConsumer<T>(_cases, _default);
		}
	}

	private final Map<Predicate<T>, Consumer<T>> _cases;
	private final Consumer<T> _defaultConsumer;
}