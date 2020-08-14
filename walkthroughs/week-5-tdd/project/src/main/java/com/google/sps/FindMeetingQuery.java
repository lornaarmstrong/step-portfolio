// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    Collection<String> attendeesList = request.getAttendees();
    long meetingDuration = request.getDuration();
    List<TimeRange> proposedTimeRanges = new ArrayList<TimeRange>();
    List<TimeRange> clashAttendeeEvents = new ArrayList<TimeRange>();

    int startDay = TimeRange.START_OF_DAY;
    int endDay = TimeRange.END_OF_DAY;

    /*loop through all the planned events and find the relevant ones 
    (where there's an attendee clash)
    */
    for (Event event: events) {
    //get the attendees from the specific event you're checking the attendees of
      Set<String> eventAttendees = event.getAttendees();
      for (String attendee : attendeesList) {
        if (eventAttendees.contains(attendee)) {
          clashAttendeeEvents.add(event.getWhen());
          break;
        }
      }
    }

    /* loop through all events attended by this meeting attendees
    to see if any of the times would clash
    */
    // if there are no attendee-clash meetings, then the whole day is available
    if (clashAttendeeEvents.size() == 0) {
        if (meetingDuration <= TimeRange.WHOLE_DAY.duration()) {
          proposedTimeRanges.add(TimeRange.WHOLE_DAY);
        }
      return (proposedTimeRanges);
    }

    // order the clashing events into start time order
    Collections.sort(clashAttendeeEvents, TimeRange.ORDER_BY_START);
    TimeRange startTime = TimeRange.fromStartEnd(startDay, clashAttendeeEvents.get(0).start(), false);

    // only add the time if there's enough duration for the requested meeting
    if (startTime.duration() >= request.getDuration()){
       proposedTimeRanges.add(startTime);
    }   

    // order by end time.
    Collections.sort(clashAttendeeEvents, TimeRange.ORDER_BY_END);
    TimeRange endTime = TimeRange.fromStartEnd(
        clashAttendeeEvents.get(clashAttendeeEvents.size()-1).end(), endDay, true);

    if (endTime.duration() >= request.getDuration()) {  
       proposedTimeRanges.add(endTime);
    }
    
    // For multiple events, there are the following cases:
    // case 1: the events don't overlap and aren't contained
    // case 2: the events overlap
    // case 3: the event is contained by another

    for (int i = 0; i < clashAttendeeEvents.size() - 1; i++) {
      TimeRange currentEvent = clashAttendeeEvents.get(i);
      TimeRange followingEvent = clashAttendeeEvents.get(i+1);


      if (!(currentEvent.overlaps(followingEvent))) {
         // case 1
        TimeRange gap = TimeRange.fromStartEnd(currentEvent.end(), followingEvent.start(), false);
        long possibleDuration = gap.duration();
        if (meetingDuration <= possibleDuration) {
          proposedTimeRanges.add(gap);
        }
      } else if (currentEvent.overlaps(followingEvent)) {
        // case 2
        // do nothing - not an eligible time
      } else if (currentEvent.contains(followingEvent) && !currentEvent.overlaps(followingEvent)) {
      // case 3
      // do nothing - not an eligible time
      }
    }

    Collections.sort(proposedTimeRanges, TimeRange.ORDER_BY_START);
    return(proposedTimeRanges);
  }
}
