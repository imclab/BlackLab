/*******************************************************************************
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nl.inl.blacklab.perdocument;

import nl.inl.blacklab.search.grouping.HitPropValue;

public class DocGroupPropertyIdentity extends DocGroupProperty {
	@Override
	public HitPropValue get(DocGroup result) {
		return result.getIdentity();
	}

	@Override
	public int compare(DocGroup a, DocGroup b) {
		if (reverse)
			return b.getIdentity().compareTo(a.getIdentity());
		return a.getIdentity().compareTo(b.getIdentity());
	}

	@Override
	public String serialize() {
		return serializeReverse() + "identity";
	}
}
